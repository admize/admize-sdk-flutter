[Admize Flutter 연동](https://github.com/admize/admize-sdk-flutter/blob/main/README.md)
====
- Flutter 앱과 연동하는 SDK 파일 용량은 1MB이며, 최신 버전의 Admize SDK 사용을 권장합니다.
- Flutter 연동 시 최신 버전의 Android Studio 사용을 권장합니다.
- Flutter 연동은 Android 5.0(Lollipop, API Level 21) 이상 기기에서 동작합니다. 
- 또한 [Google Play의 대상 API 레벨 요구사항](https://developer.android.com/distribute/best-practices/develop/target-sdk?hl=ko)을 충족하기 위해서는 targetSdkVersion 30이상, compileSdkVersion 30이상을 적용하여야 합니다.(2021년 8월부터 적용)
- Lifecycle에 따라 pause/resume/destroy API를 호출하지 않을 경우, 광고 수신에 불이익을 받을 수 있습니다.

Flutter 연동
----
Admize Android SDK를 Flutter와 연동하기 위해서는 아래와 같은 사전 절차가 필요합니다.
1) Admize SDK 연동 담당자(ops_admize@fsn.co.kr)에게 연락하여 Admize Android SDK의 Flutter 연동 요청을 합니다.
2) 연동 요청 시 매체 등록을 위해 'Bundle(Package)', 'StoreURL', 'AppName(AndroidManifest.xml 파일 <application> 태그의 label 속성값 확인)'을 연동 담당자에게 전달 합니다.
3) 매체 등록 후 담당자를 통해 APP 소유자에게 발급되는 고유 ID(publihser uid, media uid, placement uid)를 제공 받습니다.

자세한 SDK 연동 방법은 [Flutter 연동 가이드](https://github.com/admize/admize-sdk-flutter/blob/main/Flutter%20%EC%97%B0%EB%8F%99%20%EA%B0%80%EC%9D%B4%EB%93%9C.md) 에서 확인하실 수 있습니다.

공지사항 (필독)
----
안드로이드 12 신규 정책에 따라 Google 광고ID (GAID) 획득을 위해 아래와 같이 업데이트 사항을 필독 부탁드립니다.

1. Google 광고ID (GAID) 수집을 위한 퍼미션 추가 
   - manifest 파일에 아래 퍼미션을 추가해야만 안드로이드 12에서 광고ID 를 획득할 수 있습니다.
     ```xml
     <uses-permission android:name="com.google.android.gms.permission.AD_ID"/>
     ```
2. Admize SDK 최신 버전으로 업데이트
   - 1.0.0 이상 버전으로 업데이트를 해주셔야 안드로이드 12 환경에서 정상적인 광고ID 수집이 가능하며,
   - Google 광고ID 가 없는 경우에는 [App set ID](https://developer.android.com/training/articles/app-set-id) 값을 수집하여 분석 및 Fraud 방어를 문제 없이 수행 할 수 있도록 지원합니다.
   - 퍼미션 추가와 함께, Admize 안드로이드 SDK 또한 최신 버전으로 업데이트가 필요합니다.
3. 개인정보 처리방침 (Pivacy Policy) 업데이트 
   - 3rd Party SDK 가 수집하는 항목에서 디바이스 레벨의 App set ID 항목이 추가 되었습니다. 귀사 서비스의 개인정보 처리방침에 추가가 필요한 경우 업데이트를 부탁 드립니다.

안내사항
----
Google Play 의 데이터 보안정책 업데이트에 따라, 앱 개발사(자)는 Google 측에 해당 앱이 수집하는 데이터의 종류와 범위를 설문 양식에 작성하여 제출해야 합니다.

아래의 일정 참고하여 기한 안에 Play Console 에서 데이터 보안 양식 작성이 필요함을 알려드립니다.

- 22년 4월 말 : Google Play 스토어에서 보안섹션이 사용자에게 표기
- 22년 7월 20일 : 양식 제출 및 개인정보처리방침 승인 기한 (양식과 관련해 해결되지 않은 문제가 있는 경우 신규 앱 제출 및 앱 업데이트 거부될수 있습니다)

업데이트된 정책을 준수하실 수 있도록 Admize SDK 에서 수집하는 데이터 항목에 대해 안내드립니다.

Admize SDK 에서 광고 및 분석 목적으로 다음 데이터를 공유합니다.

카테고리|데이터 유형
---|---
기기 또는 기타 식별자|Android 광고 ID 를 공유
앱 활동|인스톨 된 앱
앱 정보 및 성능|오류 로그, 앱 관련 기타 진단 데이터

Admize SDK 에서 전송하는 모든 사용자 데이터는 전송 중에 암호화 되며 사용자가 데이터 삭제를 요청할 수 있는 방편을 제공하지 않습니다.

매체 운영 정책
----
[Admize 매체 운영 정책](https://admize-static.s3.ap-northeast-2.amazonaws.com/ADMIZE_%EB%A7%A4%EC%B2%B4%EC%9A%B4%EC%98%81%EC%A0%95%EC%B1%85.html) 에서 확인하실 수 있습니다.

문의
----
Admize SDK 설치 관련하여 문의 사항은 고객센터 **1544-8867** 또는
<ops_admize@fsn.co.kr> 로 문의주시기 바랍니다.
