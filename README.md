# Dirtymail : Mail-Client

### Tasks

- Login screen (Use Google API for login, like login via Google Plus, we will use Gmail for testing)
- Fetch inbox mails of the signed in Gmail account holder (Fetch recent 50 mails)
- Store emails in local database with fields like id, sender name, sender mail id, subject, content, etc (it would be nice to see if Realm is used as db, #notRequired) (fun would be if you can download attachments, *#notRequired*)
- User can delete mail (delete mails from local db) (it would be nice if it is removed from gmail account also, *#notRequired*)
- User can reply to mail (make view for sending mail) (make dummy method that sends mail to any RESTApi, response will be error) (you can send email using Google API's, *#notMandatory*)
- List item which is showing email should be swipeable like Gmail, user can delete & reply from there
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
