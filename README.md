# WebCrawler

<h3>매번 페이지 확인하기 귀찮아서 만든 자동 크롤링 앱</h3>
AlarmManager로 스케줄러 지정 -> Worker에서 WebView Crawling -> Slack API로 Message 보내기
<br><br>

structure: Jetpack compose + MVVM <br>
async: Coroutine, RxJava(previous) <br>
schedule management: AlarmManager, BroadcastReceiver, Worker <br>
storage: DataStore, SharedPreference(previous)
<br><br>
<img width="500" height="390" alt="Image" src="https://github.com/user-attachments/assets/e575ab26-10c9-4f9d-9c73-06b366de819e" />
