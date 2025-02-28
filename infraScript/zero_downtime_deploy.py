#!/usr/bin/env python3

import os
import requests  # HTTP 요청을 위한 모듈 추가
import subprocess
import time
from typing import Dict, Optional


class ServiceManager:
    # 초기화 함수
    def __init__(self, socat_port: int = 8081, sleep_duration: int = 3) -> None:
        print(f"[INIT] Initializing ServiceManager with socat_port={socat_port}, sleep_duration={sleep_duration}")
        self.socat_port: int = socat_port
        self.sleep_duration: int = sleep_duration
        self.services: Dict[str, int] = {
            'here_is_paw_1': 8082,
            'here_is_paw_2': 8083
        }
        print(f"[INIT] Configured services: {self.services}")
        self.current_name: Optional[str] = None
        self.current_port: Optional[int] = None
        self.next_name: Optional[str] = None
        self.next_port: Optional[int] = None
        print("[INIT] ServiceManager initialization complete")

    # 현재 실행 중인 서비스를 찾는 함수
    def _find_current_service(self) -> None:
        print(f"[FIND_CURRENT] Looking for currently running service on socat port {self.socat_port}...")
        cmd: str = f"ps aux | grep 'socat -t0 TCP-LISTEN:{self.socat_port}' | grep -v grep | awk '{{print $NF}}'"
        print(f"[FIND_CURRENT] Executing command: {cmd}")
        current_service: str = subprocess.getoutput(cmd)
        print(f"[FIND_CURRENT] Command output: '{current_service}'")

        if not current_service:
            print(f"[FIND_CURRENT] No active socat process found. Defaulting to here_is_paw_2 (port {self.services['here_is_paw_2']})")
            self.current_name, self.current_port = 'here_is_paw_2', self.services['here_is_paw_2']
        else:
            self.current_port = int(current_service.split(':')[-1])
            print(f"[FIND_CURRENT] Found active socat process forwarding to port {self.current_port}")
            self.current_name = next((name for name, port in self.services.items() if port == self.current_port), None)
            print(f"[FIND_CURRENT] Current service identified as: {self.current_name}")

    # 다음에 실행할 서비스를 찾는 함수
    def _find_next_service(self) -> None:
        print(f"[FIND_NEXT] Determining next service to deploy (current: {self.current_name})")
        self.next_name, self.next_port = next(
            ((name, port) for name, port in self.services.items() if name != self.current_name),
            (None, None)
        )
        print(f"[FIND_NEXT] Next service will be: {self.next_name} on port {self.next_port}")

    # Docker 컨테이너를 제거하는 함수
    def _remove_container(self, name: str) -> None:
        print(f"[REMOVE] Attempting to remove container: {name}")
        stop_cmd = f"docker stop {name} 2> /dev/null"
        print(f"[REMOVE] Executing: {stop_cmd}")
        os.system(stop_cmd)

        remove_cmd = f"docker rm -f {name} 2> /dev/null"
        print(f"[REMOVE] Executing: {remove_cmd}")
        os.system(remove_cmd)
        print(f"[REMOVE] Container {name} removal commands completed")

    # Docker 컨테이너를 실행하는 함수
    def _run_container(self, name: str, port: int) -> None:
        print(f"[RUN] Starting new container: {name} on port {port}")
        cmd = f"docker run -d --name={name} --restart unless-stopped -p {port}:8079 -e TZ=Asia/Seoul -v /dockerProjects/here_is_paw/volumes/gen:/gen --pull always ghcr.io/here-is-paw/here_is_paw"
        print(f"[RUN] Executing: {cmd}")
        result = os.system(cmd)
        print(f"[RUN] Container start command completed with result code: {result}")

    def _switch_port(self) -> None:
        # Socat 포트를 전환하는 함수
        print(f"[SWITCH] Switching socat from port {self.current_port} to {self.next_port}")
        cmd: str = f"ps aux | grep 'socat -t0 TCP-LISTEN:{self.socat_port}' | grep -v grep | awk '{{print $2}}'"
        print(f"[SWITCH] Finding existing socat process with command: {cmd}")
        pid: str = subprocess.getoutput(cmd)
        print(f"[SWITCH] Found socat process ID: '{pid}'")

        if pid:
            kill_cmd = f"kill -9 {pid} 2>/dev/null"
            print(f"[SWITCH] Terminating existing socat process: {kill_cmd}")
            os.system(kill_cmd)
            print(f"[SWITCH] Terminated socat process with PID {pid}")
        else:
            print("[SWITCH] No existing socat process found to terminate")

        print(f"[SWITCH] Waiting {5} seconds before starting new socat process...")
        time.sleep(5)

        new_socat_cmd = f"nohup socat -t0 TCP-LISTEN:{self.socat_port},fork,reuseaddr TCP:localhost:{self.next_port} &>/dev/null &"
        print(f"[SWITCH] Starting new socat process: {new_socat_cmd}")
        os.system(new_socat_cmd)
        print(f"[SWITCH] New socat process started, forwarding port {self.socat_port} to {self.next_port}")

    # 서비스 상태를 확인하는 함수
    def _is_service_up(self, port: int) -> bool:
        url = f"http://127.0.0.1:{port}/actuator/health"
        print(f"[HEALTH] Checking service health at: {url}")
        try:
            print(f"[HEALTH] Sending HTTP GET request to {url}...")
            response = requests.get(url, timeout=5)  # 5초 이내 응답 없으면 예외 발생
            print(f"[HEALTH] Received response: status_code={response.status_code}, body={response.text[:100]}")

            if response.status_code == 200 and response.json().get('status') == 'UP':
                print(f"[HEALTH] Service on port {port} is UP and healthy")
                return True
            else:
                print(f"[HEALTH] Service on port {port} returned status_code={response.status_code}, not considered healthy")
                return False

        except requests.RequestException as e:
            print(f"[HEALTH] Error checking service health: {e}")
            return False

    # 서비스를 업데이트하는 함수
    def update_service(self) -> None:
        print("[UPDATE] Starting service update process...")

        print("[UPDATE] Step 1: Identifying current service")
        self._find_current_service()

        print("[UPDATE] Step 2: Determining next service to deploy")
        self._find_next_service()

        print(f"[UPDATE] Step 3: Removing existing container for {self.next_name} if it exists")
        self._remove_container(self.next_name)

        print(f"[UPDATE] Step 4: Starting new container for {self.next_name} on port {self.next_port}")
        self._run_container(self.next_name, self.next_port)

        print(f"[UPDATE] Step 5: Waiting for {self.next_name} to become healthy...")
        attempts = 0
        # 새 서비스가 'UP' 상태가 될 때까지 기다림
        while not self._is_service_up(self.next_port):
            attempts += 1
            print(f"[UPDATE] Health check attempt #{attempts} for {self.next_name} failed. Waiting {self.sleep_duration} seconds before retrying...")
            time.sleep(self.sleep_duration)

        print(f"[UPDATE] Service {self.next_name} is now healthy after {attempts} attempts")

        print(f"[UPDATE] Step 6: Switching socat port {self.socat_port} from {self.current_port} to {self.next_port}")
        self._switch_port()

        if self.current_name is not None:
            print(f"[UPDATE] Step 7: Removing old container {self.current_name}")
            self._remove_container(self.current_name)
        else:
            print("[UPDATE] Step 7: No current container to remove")

        print("[UPDATE] Service update completed successfully!")
        print(f"[UPDATE] Summary: Switched from {self.current_name}:{self.current_port} to {self.next_name}:{self.next_port}")


if __name__ == "__main__":
    print("[MAIN] Starting ServiceManager script")
    manager = ServiceManager()
    print("[MAIN] Created ServiceManager instance, beginning update process")
    manager.update_service()
    print("[MAIN] Service update completed, script execution finished")