이 디렉토리는 애플리케이션의 서비스(Service) 레이어 클래스를 포함합니다. 
서비스 레이어는 비즈니스 로직을 구현하는 곳으로, 데이터의 검증, 여러 DAO의 조합, 트랜잭션 관리 등을 수행합니다.
컨트롤러로부터 받은 요청의 실제 비즈니스 로직을 처리하는 역할을 담당합니다.

파일 네이밍

ex) UserService, UserServiceImpl