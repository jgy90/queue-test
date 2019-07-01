# Words Separator
한 파일 내에 알파벳으로 시작하는 단어들을 대소문자 구분없는 알파벳별로 각각의 파일에 저장하는 프로그램

## 주요 고려사항
### 설계
+ 주요 기능에 대한 작업 flow가 어떻게 되는가
  + 입력 -> partition별 분배 -> 알파벳별 분배 -> 출력
+ 각각의 기능에 따라 몇 종류의 Consumer가 필요한가
  + 알파벳의 경우 26개로 26의 파일 출력이 필요하지만 Core 수의 한계로 동시에 다 쓸수가 없음
  + 출력 이외의 기능에 대해서는 추가의 Consumer를 이용
+ 부가 기능들에 대해 확장성이 있는가
  + 공통 기능이나 종류에 따른 기능들은 최대한 단순하게 추가 가능하도록 설계
+ class와 interface를 어떻게 나눌것인가
  + 일관된 기능을 수
  + 공통 기능에 대해서는 종류마다 interface 생성

### 성능
+ Producer가 Partition에 분배 시 최대한 고르게 분배할 것
+ Consumer가 Partition 접근 시 경합이 없어야 성능적으로 향상될 것
+ File 입출력을 최대한 Sequential Read / Write 로 동작하도록 필요
+ 사용 Server 혹은 PC 사용에 따라 성능 조절이 가능하도록 필요

## Class 설계


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