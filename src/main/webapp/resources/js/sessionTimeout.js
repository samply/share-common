//javascript: window.history.forward(1);

var lastActive = 0; // #{session.lastAccessedTime};
var maxtime = 0;
var stringTimeoutOne = "";
var stringTimeoutTwo = "";
var stringTimeoutUnder = "";
var stringTimeoutTwoMin = "";

function maxInitValues(la, mt, tto, ttt, ttm, ttu) {
  lastActive = la;
  maxtime = mt;
  stringTimeoutOne = tto;
  stringTimeoutTwo = ttt;
  stringTimeoutUnder = ttu;
  stringTimeoutTwoMin = ttm;
}

function delayLoad() {
  setTimeout(postLoadInit, 1000);
}

function postLoadInit() {
  timeoutFunction();
}

function resetTimer() {
  lastActive = new Date().getTime();
}

function timeoutFunction() {
  var milliseconds = new Date().getTime();

  // var maxtime = #{session.maxInactiveInterval} - 30; //inaccuracy between
  // server and JS unfortunately so safety zone of 20 seconds
  var lasttime = Math.floor((milliseconds - lastActive) / 1000);
  var resttime = maxtime - lasttime;

  if (lastActive < 1) {
    console.log('timeout script not yet loaded correctly...');
    setTimeout(timeoutFunction, 5000);
    return;
  }
  var modalOn = $('#timeoutSession').hasClass('in');

  // When the modal is first going to be shown,
  if (resttime <= 100 && !modalOn) {
    if (!$("body").hasClass('broker')) {
      // if we are in page that needs to be backupped, perform backup in local storage
      if (typeof (performSerializationOnServerAndStoreResultLocally)
          === 'function') {
        performSerializationOnServerAndStoreResultLocally();
      }
      // or if we are in another page containing only values to be stored
      else if (document.getElementById("localValuesForm") != null) {
        writeValuesToLocalStorage();
      }
    }
    $('#timeoutSession').modal({
      keyboard: false,
      backdrop: false
    });
  }

  if (resttime <= 0) {
    if (typeof (storeAndLogout) == "function") {
      storeAndLogout();
    } else {
      $('.logoutElement a').click();
    }
    return true;
  }

  if (document.getElementById('dialog-countdown') != null) {
    document.getElementById('dialog-countdown').innerHTML = stringTimeoutOne
        + " " + resttime + " " + stringTimeoutTwo;
  }

  if (resttime <= 100) {
    setTimeout(timeoutFunction, 1000);
  } else {
    setTimeout(timeoutFunction, 5000);
  } // run every 5 seconds, later every
    // second
}

window.onload = delayLoad();
