$(document).ready(function () {

  var ops = ["IS_NULL", "IS_NOT_NULL"];
  $('#searchIntro').on('hide.bs.collapse', function () {
    $(this).find('i').removeClass('fa-remove').addClass('fa-question');
  });
  $('#searchIntro').on('show.bs.collapse', function () {
    $(this).find('i').removeClass('fa-question').addClass('fa-remove');
  });

  $(".query-builder-panel").on("change", ".operatorSelect", function () {
    var operator = $(this).val();
    $('.saved').hide();
    if ($.inArray(operator, ops) > -1) {
      $(this).parentsUntil('.row').parent().find('.values :input').prop(
          'disabled', true);
      $(this).parentsUntil('.row').parent().find('.values').hide();
    } else {
      $(this).parentsUntil('.row').parent().find('.values :input').prop(
          'disabled', false);
      $(this).parentsUntil('.row').parent().find('.values').show();
    }
    if (operator == "BETWEEN") {
      $(this).parentsUntil('.row').parent().find('.valuesSingle').hide();
      $(this).parentsUntil('.row').parent().find('.valuesBetween').show();
    } else {
      $(this).parentsUntil('.row').parent().find('.valuesBetween').hide();
      $(this).parentsUntil('.row').parent().find('.valuesSingle').show();
    }
  });

  makePanelsDroppable();

  $('.project-panel').click(function () {
    $(this).find('.go-to-project a').trigger("click");
  });

});

var onItemDropped = function onItemDropped(data) {
  if (data.status == "begin") {
    $('div.sortableItemPanel').block(
        {message: null, overlayCSS: {opacity: 0.5}});
  } else if (data.status == "complete") {
    $('div.sortableItemPanel').unblock();
  } else if (data.status == "success") {
    $('div.sortableItemPanel').unblock();
  }
};

function makePanelsDroppable() {
  $(".sortableItemPanel .conjunctionGroupItem").droppable({
    accept: '.draggableItem',
    greedy: true,
    activeClass: 'animated pulse droppableActive',
    hoverClass: 'droppableHover',
    drop: function (event, ui) {
      $('.saved').hide();
      $('div.sortableItemPanel').block(
          {message: null, overlayCSS: {opacity: 0.5}});
      var mdrid = $(ui.draggable).find('#mdrId').val();
      var tmpid = $(this).find('#tempId').val();
      var searchString = $(ui.draggable).find('#searchString').val();

      if (typeof (searchString) === "undefined") {
        searchString = "";
      }

      if (typeof (tmpid) === "undefined") {
        addItemToQueryRoot({'mdrId': mdrid, 'searchString': searchString});
      } else {
        addItemToQueryGroup(
            {'mdrId': mdrid, 'searchString': searchString, 'tmpId': tmpid});
      }
    }
  });
}
