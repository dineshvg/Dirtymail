# Dirtymail : Mail-Client

### Third party information 

- Logo generated using https://logomakr.com/3oLPmD

https://drive.google.com/file/d/0B91v01fv4vZmb0N6ZkwzV0hzV28/view?usp=sharing

### Status 
[Status.pdf](http://nbviewer.jupyter.org/github/dineshvg/Dirtymail/blob/master/MailApplicationReportforSevenREJobapplication.pdf)


### Application SCreenshots
<img src="https://github.com/dineshvg/Dirtymail/blob/master/Screenshots/Screenshot_20161026-061253.png" width="140">
<img src="https://github.com/dineshvg/Dirtymail/blob/master/Screenshots/Screenshot_20161026-061304.png" width="140">
<img src="https://github.com/dineshvg/Dirtymail/blob/master/Screenshots/Screenshot_20161026-072357.png" width="140">
<img src="https://github.com/dineshvg/Dirtymail/blob/master/Screenshots/Screenshot_20161026-081615.png" width="140">
<img src="https://github.com/dineshvg/Dirtymail/blob/master/Screenshots/Screenshot_20161026-081627.png" width="140">

<img src="https://github.com/dineshvg/Dirtymail/blob/master/Screenshots/Screenshot_20161026-081636.png" width="140">
<img src="https://github.com/dineshvg/Dirtymail/blob/master/Screenshots/Screenshot_20161026-081644.png" width="140">


### Screens

- Splash screen that stays for 2 seconds before opening the login screen.
- Login screen that asks for the google login using Gmail API for the first time.
- Mail list screen that shows 50 conversations in a Recycler view.(A conversation can have many mails)
- Detailed mail screen that shows the body of the mail along with other information similar to Google mail.
- Send mail shows the place where we can type our mail.

### Functions 
- Fetching mails with Label:INBOX and showing them.
- Mails stored in local Realm database with different fields. (Dirtymail and DirtymailContent have the fields)
- Mails can be deleted induvidually from the database. (Local delete) (Error in recycler view, needs to be fixed)
- Swipable mails.
- Send mail screen. On trying to send mail we get a 400 bad request.
- Background Service to call API every 15 minutes.

### External Libraries used
- Realm
- SwipeRevealLayout

### TODO (For later id required)

- Delete mail from Gmail *#notMandatory*
- (you can send email using Google API's, *#notMandatory*
- Have a manual sync by scrolling top to bottom like Gmail


### Tips
- Use Github to find some useful code or library
- Popular libraries for use are Dagger2, Retrofit, OkHttp, EventBus, Realm, LeakCanary, Butterknife; use some of these to prove competency in working with third party code
- Swipe list item can look like [this] 
- We love to have beautiful UI. Find some nice UI view or animations online to impress us
- Using some test cases can be nice
- Explore MVP pattern and try to follow it, many examples are available online
 
[this]: <http://i.stack.imgur.com/aB55l.png>
