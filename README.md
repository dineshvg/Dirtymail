# Dirtymail : Mail-Client

### Third party information 

- Logo generated using https://logomakr.com/
- Dotted metalbackground from freepik.com. Designed by Layerace / Freepik. License allowed for commercial use.

### Screens

- Splash screen that stays for 2 seconds before opening the login screen.
- Login screen that asks for the google login using Gmail API for the first time.
- Mail list screen that shows 50 conversations in a Recycler view.(A conversation can have many mails)
- Detailed mail screen that shows the body of the mail along with other information similar to Google mail.

### Functions 
- Fetching mails with Label:INBOX and showing them.
- Mails stored in local Realm database with different fields. (Dirtymail and DirtymailContent have the fields)
- Mails can be deleted induvidually from the database.
- Swipable mails.

### TODO
- Delete mail from Gmail
- User can reply to mail (make view for sending mail) (make dummy method that sends mail to any RESTApi, response will be error) (you can send email using Google API's, *#notMandatory*)
- Sync mails every 15 minutes and have a manual sync by scrolling top to bottom like Gmail
To send mail, use HTTP post method to add all contents or simply make Rest call as shown below
```sh
http://www.random-server.com/send?subject=MySubject&sender=temp@var.com&timeStamp=11112342234&body=MyTrialMail
```

### Tips
- Use Github to find some useful code or library
- Popular libraries for use are Dagger2, Retrofit, OkHttp, EventBus, Realm, LeakCanary, Butterknife; use some of these to prove competency in working with third party code
- Swipe list item can look like [this] 
- We love to have beautiful UI. Find some nice UI view or animations online to impress us
- Using some test cases can be nice
- Explore MVP pattern and try to follow it, many examples are available online
 
[this]: <http://i.stack.imgur.com/aB55l.png>
