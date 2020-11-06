$(document).ready(function () {
  $(".itemSelectBox").select2({
    placeholder: "Keyword",
    allowClear: true,
    maximumSelectionSize: 4
  });

  $('.multiLevelMenu').on('click', '.collapse.in', function () {
    $(this).removeClass('in');
  });

  createPopovers();
});

// on enter pressed, execute search

$('.itemSearchPanel').keypress(function (e) {
  if (e.which == 13) {
    $('.searchButton').click();
    e.stopImmediatePropagation();
    e.preventDefault();
    return false;
  }
});

var onItemSearchExpand = function onItemSearchExpand(data) {
  if (data.status == "begin") {
    $('div.itemNavigationPanel').block(
        {message: null, overlayCSS: {opacity: 0.5}});
  } else if (data.status == "complete") {
    $('div.itemNavigationPanel').unblock();
  } else if (data.status == "success") {
    $('div.itemNavigationPanel').unblock();
  }
};

function createPopovers() {
  $('[data-toggle="popover"]').popover({
    html: true,
    delay: {"show": 500, "hide": 100},
    trigger: 'hover'
  });
};
