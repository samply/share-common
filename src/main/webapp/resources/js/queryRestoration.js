if (typeof (restorationProperties) == "undefined") {
  new Error("Restoration properties need to be defined for query restoration");
}

function writeValuesToLocalStorage() {
  console.log("write to local storage");
  for (var localStorageKey in restorationProperties) {
    // get value that should be saved from view
    var valueToSave = $(restorationProperties[localStorageKey]).val();
    if (valueToSave === "" || valueToSave === null) {
      localStorage.removeItem(localStorageKey); // don't save empty strings or nulls
    } else { // save value in local storage
      localStorage.setItem(localStorageKey, valueToSave);
    }
  }
}

function moveValuesFromLocalStorageToDom() {
  console.log("read from local storage");
  for (var localStorageKey in restorationProperties) {
    // Special Case for the site selection. In this case, tokenize the string and reinitialize select2 plugin
    if (localStorageKey == "samply.share.broker.query.sites") {
      if (localStorage.getItem(localStorageKey) != null) {
        $(restorationProperties[localStorageKey]).val(
            localStorage.getItem(localStorageKey).split(','));
      }
      createSiteSelect();
    } else if (localStorageKey == "samply.share.broker.query.expose") {
      loadExposeWithId({expose_id: localStorage.getItem(localStorageKey)});
    } else {
      $(restorationProperties[localStorageKey]).val(
          localStorage.getItem(localStorageKey));
    }
  }
}

function deleteValuesFromLocalStorage() {
  console.log("delete from local storage");
  if (typeof (restorationProperties) == "undefined") {
    return;
  }
  for (var localStorageKey in restorationProperties) {
    localStorage.removeItem(localStorageKey);
  }
}

function valuesInLocalStorage() {
  for (var localStorageKey in restorationProperties) {
    var localStorageItem = localStorage.getItem(localStorageKey);
    if (localStorageItem != null && localStorageItem.length > 0) {
      return true;
    }
  }
  return false;
}

function dismissValues() {
  $('#pleaseWait').modal('show');
  deleteValuesFromLocalStorage();
  location.reload();
}
