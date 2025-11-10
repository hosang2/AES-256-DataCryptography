# AES-256-DataCryptography
DataCryptography (AES-256 : CBC,GCM)

aes 암호화 ref용 클래스

암호화 정보 생성 ===========================================================

DataCryptography.class 추가 , CRYPTO table 추가 

암호화 요청시 

dataEx => Sha256 단방향 암호화  => HEX Byte(16)로 변환 하여 키값 생성 

Mode CBC 
"exText" + 요청한 DateTime(yyyyMMddHHmmssSS) 로 InitializationVector 생성 Byte(16)/PKCS5Padding
Mode GCM 
"exText" + 요청한 DateTime(yyyyMMddHHmmssSS) 로 InitializationVector 생성 Byte(12)/NoPadding

CRYPTO Table 예)

암호화 정보 (
sampleDataX, sampleDataY, sampleDataZ, IV생성시 사용한 DataTime(yyyyMMddHHmmssSS) 
Select_Key(IV 생성시 사용한 DataTime을 Sha256 암호화 다시 암호화 후 생성한 유니크키 )  저장

** 암호화시 해당 Data Row 암복호화에 사용한 키값은 동일 , row의 하위 파라미터 별 암호화시 IV 값은 각각 별도 
=======================================================================

복호화 요청시 
sampleDataX + sampleDataY + sampleDataZ / 또는 Select_key 로 암호화 정보 조회 

해당 정보로 암호화시 사용한 키값 생성 => IV값 생성 => 복호화 

=======================================================================
*현재 CBC mode , GCM mode 암복호화 구현 선택하여 사용 ( GCM mode 추천 )




# Notes


CBC

블록 단위로 암호화 (AES 16바이트 블록)

별도의 IV 필요

무결성 검증 불가 → 데이터 위변조 탐지 불가능

AES/CBC/PKCS5Padding 사용

---------------------------------------------------------------------------------------------------------------------------
GCM (Galois/Counter Mode)

스트림 기반 처리 → 빠름

IV (12바이트 추천) 필요

무결성 검증 (Authentication Tag) 지원 → 해킹/위변조 탐지 가능

AES/GCM/NoPadding 사용

복호화 시 반드시 Tag까지 포함해서 처리해야 함
