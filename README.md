# Words Separator
한 파일 내에 알파벳으로 시작하는 단어들을 대소문자 구분없는 알파벳별로 각각의 파일에 저장하는 프로그램

## 주요 고려사항
### 설계
+ 주요 기능에 대한 작업 flow가 어떻게 되는가
  + 입력 -> partition별 분배 -> 알파벳별 분배 -> 출력
+ 주요 기능에 대해 행위가 무엇이 있는가
  + Queue 송수신, 파일 입출력, 리소스 정리, partition 선정, 입력값 유효성 검사  
+ 각각의 기능에 따라 몇 종류의 Consumer가 필요한가
  + 알파벳의 경우 26개로 26의 파일 출력이 필요
  + 26개의 consumer로 할 경우 놀게되는 partition이 많아짐 (consumer는 정해진 partition만 참조)
  + HASH 기준으로 partition에 분배할 경우 이를 알파벳으로 분류해줄 것이 필요
+ class와 interface를 어떻게 나눌것인가
  + 공통 기능에 대해서는 종류마다 class생성 
  + 추상적인 기능동작에 대해서는 interface 생성 후 class에서 implements  
+ 확장성을 위해 generic 사용 필요
  + 공통 행위를 정의할 interface에서는 parameter를 

### 성능
+ Producer가 Partition에 분배 시 최대한 고르게 분배할 것
+ Consumer가 Partition 접근 시 경합이 없어야 성능적으로 향상될 것
+ File 입출력을 최대한 Sequential Read / Write 로 동작하도록 필요
+ 사용 Server 혹은 PC 사용에 따라 성능 조절이 가능하도록 필요

## Class 설계
### Class Diagram
![class_diagram](https://user-images.githubusercontent.com/52350273/60509898-ffe4e180-9d08-11e9-95d0-f800f6d4261d.png)

### Class 설명
`WordSeparatorStarter`
+ 시작용 class  
+ 입력값을 확인하고 Partition  생성 후 Producer와 Consumer들을 실행

`WordsParserProducer`
+ 단어 분배용 Producer
+ 파일에서 단어를 읽어 유효성 검사 후 설정된 방식에 따라 Partition에 분배
+ `QueueSender`와 `WordReader`를 사용
+ `ResourceClean`을 implements

`WordIntermediaryConsumer`
+ 중계 Consumer
+ 정해진 Partition에서 단어를 알파벳에 맞는 파일 출력을 위한 Queue로 전달
+ `QueueSender`와 `QueueReceiver`를 사용
+ `ResourceClean`을 implements

`WordSaveConsumer`
+ 출력 Consumer
+ Queue에서 단어를 받아 파일로 출력
+ `QueueReceiver`와 `WordWriter`를 사용
+ `ResourceClean`을 implements

`QueueSender`
+ 단어 전송기
+ Queue로 단어를 전송
+ `Sendable`을 implements

`QueueReceiver`
+ 단어 수신시
+ Queue에서 단어를 수신
+ `Receivable`을 implements

`WordReader`
+ 단어 입력기
+ 파일로부터 단어를 입력
+ `Readable`을 implements

`WordWriter`
+ 단어 출력기
+ 파일로 단어를 출력
+ `Writable`을 implements

### Interface 설명
`Sendable`: 
+ 전송용 인터페이스

`Receivable`: 
+ 수신용 인터페이스

`Readable`: 
+ 읽기용 인터페이스
+ `ResourceClean`을 implements

`Writable`
+ 쓰기용 인터페이스
+ `ResourceClean`을 implements

`ResourceClean`:
+ 리소스 정리용 인터페이스 


## 사용방법
1. MAVEN install을 이용하여 jar생성
2. java -jar {file_path} {입력 단어 위치} {저장 폴더 위치} {partition 수}

## 셋팅값
`SettingVariables.java` 파일 내 위치
### 주요 셋팅값
`wordsFilePath`: 입력 파일 경로

`resultFolderPath`: 저장 폴더 경로

`numOfWordPartitions`: partition 수

`numberOfIntermediaryConsumer`: 중계 Consumer 수

`flushIOCount`: 동시 SoftFlush 수

`outputBufferSizeMultiplier`: Write Buffer 승수 (Windows: 8kb * `outputBufferSizeMultiplier`, Linux, Mac: 4kb * `outputBufferSizeMultiplier`) 

`outputBufferLowerLimitPercent`: SoftFlush 기준점 (단위: %), Write Buffer에 할당되는 메모리 크기 기준

`wordPartitionsTimeout`: 중계 Consumer sleep 시간 (단위: ms)

`savePartitionsTimeout`: 저장 Consumer sleep 시간 (단위: ms)

`distributionType`: Producer내 partition 분배방식 (HASH, ALPHABET, FNV1A)

`fnv1aMaxLength`: FNV1a Hash 대상 최대 String 개수