/* activate multi level items menu*/
$(function () {
  $('.multiLevelMenu').metisMenu();
});

/* enable dragging items to the sortable panel */
$(".draggableItem").draggable({
  // helper clone causes some trouble with scrolling...
//    helper : "clone",
  helper: function (event) {
    return $(
        "<div class='ui-widget-header'>" + $(this).find('.designation').html()
        + "</div>");
  },
  appendTo: "body",
  refreshPositions: true,
  revert: "invalid"
});

//close item group when it is open and it was clicked again
$(document).ready(function () {
  $('a.searchMdrGroup').has('i.fa-folder-open-o').click(function () {
    $(this).find('i.fa-folder-open-o').removeClass("fa-folder-open-o").addClass(
        "fa-folder-o");
    return false;
  });
});
