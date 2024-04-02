<p align="center">
  <img src="https://velog.velcdn.com/images/hj_/post/f9d065e4-34d8-4e3d-8acd-abdf8808a7f0/image.png">
</p>
<hr>

<div align="center">
  다른 사람들과 공유하고 싶은 산책 경로를 업로드하고, 이를 챌린지 형식으로 참여할 수 있는 인증 사진 기반 챌린지 서비스 VitalRoutes 입니다.
</div>

<br><br>


# 🛠 기술 스택

- Language : Java 17
- Framework : SpringBoot 3.2.2, SpringSecurity, Spring Data JPA, QueryDSL
- DB : MariaDB
- Server : Ubuntu, Nginx, Github Actions, Docker
- ETC : Kakao Login API, Kakao Local API, Google SMTP, Firebase




<br><br>


# 📌 ERD

<p align="center">
  <img src="https://velog.velcdn.com/images/hj_/post/f2e7f948-03da-47bd-b47c-ea0c5244abec/image.png">
</p>


<br><br>


# 🔎 화면 구성


|챌린지 등록 페이지|챌린지 상세 페이지|
|:--------:|:--------:|
|<img src="https://velog.velcdn.com/images/hj_/post/d6b483b2-dae0-49fc-8411-25a3ed6059f3/image.png"/>|<img src="https://velog.velcdn.com/images/hj_/post/2fd7bcb0-8d59-4056-92df-450a72f59eda/image.png"/>|
|- 위치정보를 가진 사진을 업로드하여 등록할 수 있습니다. <br>- 제목, 내용, 이동방법, 태그가 포함됩니다. |- 챌린지에 대한 정보와 참여한 게시글들을 확인할 수 있습니다. <br>-  챌린지 Spot 과 동일한 사진을 찍어 챌린지에 참여할 수 있습니다. <br>- 하단의 Input 으로 참여 게시글에 댓글을 작성할 수 있습니다.<br> - 대댓글 보기를 누르면 아래처럼 댓글을 확인할 수 있습니다.|

<br>

|챌린지 목록 페이지|챌린지 수정 페이지|
|:---:|:---:|
|<img src="https://velog.velcdn.com/images/hj_/post/5a627dd8-0920-4cca-977f-12415f044afd/image.png"/>|<img src="https://velog.velcdn.com/images/hj_/post/0dce22bc-5e58-4796-912d-a8a7743a30e7/image.png"/>|
|프로필 페이지|프로필 수정 페이지|
|<img src="https://velog.velcdn.com/images/hj_/post/ad588385-1d06-4e6b-affb-fcda89a0234c/image.png"/>|<img src="https://velog.velcdn.com/images/hj_/post/cdd64d1f-03cc-446e-9bf4-314fbc221861/image.png"/>|



<br><br>


# 🎯 주요 기능

### ⭐ 챌린지 목록
* 작성된 모든 챌린지들을 확인할 수 있습니다.
* 상단의 **신규, 인기순, 도보, 자전거** 버튼을 통해 필터를 지정할 수 있습니다.
* 또한 검색바를 통해 **제목, 지역, 도로명 주소**를 검색할 수 있습니다.


### ⭐ 챌린지 등록
* 위치 정보가 있는 사진을 업로드하면 지도에 해당 사진에 위치가 표시됩니다.
* 제목, 내용, 이동방법, 태그를 선택해서 저장할 수 있습니다.

### ⭐ 챌린지 참여
* 챌린지 상세 페이지 중간의 Input 과 사진 업로드를 통해 챌린지에 참여할 수 있습니다.
* 참여 시 각 Spot 사진에는 기존 챌린지 Spot 과 *5m* 이내인 위치정보를 가진 사진들만 업로드가 가능합니다.


### ⭐ 댓글 작성
* 챌린지 참여 하단의 Input 박스로 댓글을 작성할 수 있습니다.
* 댓글을 확인하기 위해서는 대댓글 보기 버튼을 클릭해야 합니다.

### ⭐ 회원
* 회원가입, JWT 기반 로그인, 소셜 로그인을 제공합니다.
* 프로필 이미지 및 정보 수정이 가능합니다.
* 비밀번호를 잊어버렸을 시, 가입한 이메일을 통해 비밀번호 재설정 링크를 전달 받습니다.

### ⭐ ETC

* 참여 게시글과 댓글은 숨기기 기능을 제공합니다.
* 챌린지, 참여 게시글, 댓글 신고 기능을 제공합니다.
* 챌린지와 참여는 사진을 제외하고 수정이 가능합니다.


<br><br>



# 🖥️ 서버 구성도

<p align="center">
  <img src="https://velog.velcdn.com/images/hj_/post/f805b979-41f7-43c5-b30c-66b44eebffdd/image.PNG">
</p>


<br><br>


# 🔗 링크
> 배포 URL : https://hj-vital-routes.vercel.app <br>
> 프론트 개선 Repository : https://github.com/HwaJong-N/VitalRoutes_v1.1_frontend <br>


> 프론트 원본 Repository : https://github.com/VitalRoutes/frontend <br>
> 백엔드 협업 Repository : https://github.com/VitalRoutes/backend <br>
