<!-- side menu start -->   
 
$(document).ready(function() {
  $('#demo').sidr();
});
 
<!--menu start -->   

$(function() {
	var Accordion = function(el, multiple) {
		this.el = el || {};
		this.multiple = multiple || false;

		// Variables privadas
		var links = this.el.find('.link');
		// Evento
		links.on('click', {el: this.el, multiple: this.multiple}, this.dropdown)
	}

	Accordion.prototype.dropdown = function(e) {
		var $el = e.data.el;
			$this = $(this),
			$next = $this.next();

		$next.slideToggle();
		$this.parent().toggleClass('open');

		if (!e.data.multiple) {
			$el.find('.submenu').not($next).slideUp().parent().removeClass('open');
		};
	}	

	var accordion = new Accordion($('#accordion'), false);
});
 
<!--menu start -->   
 
<!-- side menu end -->  



 <!--Datatable for shedule start-->
 
$(document).ready(function() {
    $('#example').DataTable( {
          "dom": '<"clear">rt<"bottom"i><"bottom"flp>',
		  "columnDefs": [ {
          "targets": 'no-sort',
          "orderable": false,
    } ]
    } ); 
} );

 
$(document).ready(function() {
    var dataTable = $('#example').DataTable( {      
    } );
 
    $("#filterbox").keyup(function() {
        dataTable.search(this.value).draw();
    });   
	
	$('#example').DataTable( {
        responsive: true
    } );
	 
});
 
 

$(document).ready(function() {
    $('#example02').DataTable( {
          "dom": '<"clear">rt<"bottom"i><"bottom"flp>',
		  "columnDefs": [ {
          "targets": 'no-sort',
          "orderable": false,
    } ]
    } ); 
} );

 
$(document).ready(function() {
    var dataTable = $('#example02').DataTable( {      
    } );
 
    $("#filterbox").keyup(function() {
        dataTable.search(this.value).draw();
    });   
	
	$('#example02').DataTable( {
        responsive: true
    } );
	 
});
  
 
<!--Datatable for shedule end-->

<!--APL easyResponsiveTabs start-->

    $(document).ready(function() {
        //Horizontal Tab
        $('#parentHorizontalTab').easyResponsiveTabs({
            type: 'default', //Types: default, vertical, accordion
            width: 'auto', //auto or any width like 600px
            fit: true, // 100% fit in a container
            tabidentify: 'hor_1', // The tab groups identifier
            activate: function(event) { // Callback function if tab is switched
                var $tab = $(this);
                var $info = $('#nested-tabInfo');
                var $name = $('span', $info);
                $name.text($tab.text());
                $info.show();
            }
        });

        // Child Tab
        $('#ChildVerticalTab_1').easyResponsiveTabs({
            type: 'vertical',
            width: 'auto',
            fit: true,
            tabidentify: 'ver_1', // The tab groups identifier
            activetab_bg: '#fff', // background color for active tabs in this group
            inactive_bg: '#F5F5F5', // background color for inactive tabs in this group
            active_border_color: '#c1c1c1', // border color for active tabs heads in this group
            active_content_border_color: '#5AB1D0' // border color for active tabs contect in this group so that it matches the tab head border
        });
 
    });
	
<!--APL easyResponsiveTabs end-->


<!--APL list table start-->
$(document).ready(function(){
  $('.collaptable').aCollapTable({ 
    startCollapsed: true,
    addColumn: false, 
    plusButton: '<span class="i"><i class="fa fa-plus"></i> </span>', 
    minusButton: '<span class="i"><i class="fa fa-minus"></i></span>' 
  });
});
<!--APL list table end-->


 

<!--APL Add location start-->

var current_fs, next_fs, previous_fs; //fieldsets
var left, opacity, scale; //fieldset properties which we will animate
var animating; //flag to prevent quick multi-click glitches

$(".next").click(function(){
	if(animating) return false;
	animating = true;
	
	current_fs = $(this).parent();
	
 
	next_fs = $(this).parent().next();
	
	//activate next step on progressbar using the index of next_fs
	$("#progressbar li").eq($("fieldset").index(next_fs)).addClass("active");
	
	//show the next fieldset
	next_fs.show(); 
	//hide the current fieldset with style
	current_fs.animate({opacity: 0}, {
		step: function(now, mx) {
			//as the opacity of current_fs reduces to 0 - stored in "now"
			//1. scale current_fs down to 80%
			scale = 1 - (1 - now) * 0.2;
			//2. bring next_fs from the right(50%)
			left = (now * 50)+"%";
			//3. increase opacity of next_fs to 1 as it moves in
			opacity = 1 - now;
			current_fs.css({
        'transform': 'scale('+scale+')',
        'position': 'absolute'
      });
			next_fs.css({'left': left, 'opacity': opacity});
		}, 
		duration: 800, 
		complete: function(){
			current_fs.hide();
			animating = false;
		}, 
		//this comes from the custom easing plugin
		easing: 'easeInOutBack'
	});
});

$(".previous").click(function(){
	if(animating) return false;
	animating = true;
	
	current_fs = $(this).parent();
	previous_fs = $(this).parent().prev();
	
	//de-activate current step on progressbar
	$("#progressbar li").eq($("fieldset").index(current_fs)).removeClass("active");
	
	//show the previous fieldset
	previous_fs.show(); 
	//hide the current fieldset with style
	current_fs.animate({opacity: 0}, {
		step: function(now, mx) {
			//as the opacity of current_fs reduces to 0 - stored in "now"
			//1. scale previous_fs from 80% to 100%
			scale = 0.8 + (1 - now) * 0.2;
			//2. take current_fs to the right(50%) - from 0%
			left = ((1-now) * 50)+"%";
			//3. increase opacity of previous_fs to 1 as it moves in
			opacity = 1 - now;
			current_fs.css({'left': left});
			previous_fs.css({'transform': 'scale('+scale+')', 'opacity': opacity});
		}, 
		duration: 800, 
		complete: function(){
			current_fs.hide();
			animating = false;
		}, 
		//this comes from the custom easing plugin
		easing: 'easeInOutBack'
	});
});

$(".submit").click(function(){
	return false;
})
 
<!--APL Add location end-->



<!--APL Filtering start-->

    var demo1 = $('select[name="duallistbox_demo1[]"]').bootstrapDualListbox();
    $("#demoform").submit(function() {
      alert($('[name="duallistbox_demo1[]"]').val());
      return false;
    });
	
<!--APL Filtering end-->


<!--pie chart start-->

		var doughnutData = [
				{
					value: 18,
					label: "Reached ",
					color:"#96d24c"
				},
				{
					value: 12,
					label: "Boarded ",
					color:"#f8a20f"
				} 
			];
			
			var doughnutData2 = [
				{
					value: 60,
					label: "Reached ",
					color:"#96d24c"
				},
				{
					value: 20,
					label: "Boarded ",
					color:"#f8a20f"
				} 

			];
			
			var doughnutData3 = [
				{
					value: 30,
					label: "Reached ",
					color:"#96d24c"
				},
				{
					value: 40,
					label: "Boarded ",
					color:"#f8a20f"
				} 

			];
			
			var doughnutData4 = [
				{
					value: 22,
					label: "Reached ",
					color:"#96d24c"
				},
				{
					value: 50,
					label: "Boarded ",
					color:"#f8a20f"
				} 

			];
			
			var doughnutData5 = [
				{
					value: 30,
					label: "Reached ",
					color:"#96d24c"
				},
				{
					value: 57,
					label: "Boarded ",
					color:"#f8a20f"
				} 

			];
			
			var doughnutData6 = [
				{
					value: 22,
					label: "Reached ",
					color:"#96d24c"
				},
				{
					value: 50,
					label: "Boarded ",
					color:"#f8a20f"
				} 

			];
			
			

			window.onload = function(){
				var ctx = document.getElementById("chart-area").getContext("2d"); 
				var ctx2 = document.getElementById("chart-area2").getContext("2d"); 
				var ctx3 = document.getElementById("chart-area3").getContext("2d");
				var ctx4 = document.getElementById("chart-area4").getContext("2d");
				var ctx5 = document.getElementById("chart-area5").getContext("2d");
				var ctx6 = document.getElementById("chart-area6").getContext("2d");
				
				window.myDoughnut = new Chart(ctx).Doughnut(doughnutData, {responsive : true});
				window.myDoughnut2 = new Chart(ctx2).Doughnut(doughnutData2, {responsive : true});
				window.myDoughnut3 = new Chart(ctx3).Doughnut(doughnutData3, {responsive : true});
				window.myDoughnut4 = new Chart(ctx4).Doughnut(doughnutData4, {responsive : true});
				window.myDoughnut5 = new Chart(ctx5).Doughnut(doughnutData5, {responsive : true});
				window.myDoughnut6 = new Chart(ctx6).Doughnut(doughnutData6, {responsive : true});
			};

<!--pie chart end-->
 

<!--pagination apl start-->

    $(document).ready( function () {
        $('#bootstrap-table').bdt();
    });
 
<!--pagination apl end-->


 
 
 
 
 
 
 <!--Start all Popup -->
 
  /**/$(document).ready(function() {
   $("a[rel=example_group]").fancybox({
    'transitionIn'  : 'elastic',
    'transitionOut'  : 'elastic',
    'titlePosition'  : 'none'
   });
    
  });
  
     function block_add_mainapl(){
 $('#add-apl-main').fadeIn();
 
}
  function hide_add_mainapl(){
 $('#add-apl-main').fadeOut();
 
}
  
  
    function block_apl_maps(){
 $('#apl-maps').fadeIn();
 
}
  function hide_apl_maps(){
 $('#apl-maps').fadeOut();
 
}

    function block_add_apl(){
 $('#add-apl-config').fadeIn();
 
}
  function hide_add_apl(){
 $('#add-apl-config').fadeOut();
 
} 
 
 
     function block_edit_apl(){
 $('#edit-apl-config').fadeIn();
 
}
  function hide_edit_apl(){
 $('#edit-apl-config').fadeOut();
 
}
 
      function block_audit_loginfo(){
 $('#audit-log-info').fadeIn();
 
}
  function hide_audit_loginfo(){
 $('#audit-log-info').fadeOut();
 
}
 
  function block_allocation(){
 $('#vehicle-allocation-details').fadeIn();
 
}
  function hide_allocation(){
 $('#vehicle-allocation-details').fadeOut();
 
}
 
 function block_adhoc_request(){
 $('#adhoc-request-details').fadeIn();
 
}
 
  function hide_adhoc_request(){
 $('#adhoc-request-details').fadeOut();
 
}

  function block_current_tripstatus(){
 $('#current-trip-status').fadeIn();
 
}

function hide_current_tripstatus(){
 $('#current-trip-status').fadeOut();
 
}
 
  function block_womenemployee_status(){
 $('#womenemployee-travel-status').fadeIn();
 
}

  function hide_womenemployee_status(){
 $('#womenemployee-travel-status').fadeOut();
 
}

  function block_allemployee_travelstatus(){
 $('#all-employee-travel-status').fadeIn();
 
}

function hide_allemployee_travelstatus(){
 $('#all-employee-travel-status').fadeOut();
 
}


 function block_trip_status(){
 $('#trip-status').fadeIn();
 
}


 function hide_trip_status(){
 $('#trip-status').fadeOut();
 
}


 function block_auditlog_info(){
 $('#auditlog_info').fadeIn();
 
}


 function hide_auditlog_info(){
 $('#auditlog_info').fadeOut();
 
}

 function block_schedule_upload(){
 $('#schedule_upload').fadeIn();
 
}


 function hide_schedule_upload(){
 $('#schedule_upload').fadeOut();
 
}

 function block_add_subscription(){
 $('#add_subscription').fadeIn();
 
}


 function hide_add_subscription(){
 $('#add_subscription').fadeOut();
 
}

 function block_search_emp(){
 $('#search_emp').fadeIn();
 
} 
 function hide_search_emp(){
 $('#search_emp').fadeOut();
 
}

 function block_selectAPL(){
 $('#selectAPL').fadeIn();
 
} 
 function hide_selectAPL(){
 $('#selectAPL').fadeOut();
 
}


 function block_edit_subscription(){
 $('#edit_subscription').fadeIn();
 
}
 function hide_edit_subscription(){
 $('#edit_subscription').fadeOut();
 
}

function block_add_adhoc(){
 $('#add_adhoc').fadeIn();
}
 function hide_add_adhoc(){
 $('#add_adhoc').fadeOut();
}

function block_edit_adhoc(){
 $('#edit_adhoc').fadeIn();
}
 function hide_edit_adhoc(){
 $('#edit_adhoc').fadeOut();
}

function block_approve_adhoc(){
 $('#approve_adhoc').fadeIn();
}
 function hide_approve_adhoc(){
 $('#approve_adhoc').fadeOut();
}

function block_edit_assign_role(){
 $('#edit_assign_role').fadeIn();
}
 function hide_edit_assign_role(){
 $('#edit_assign_role').fadeOut();
}
 
 
 
 
 
 
 
<!--onload Popup--> 
  function hide_enq(){
 $('#popper-cab-details').fadeOut();
 
}  
<!--onload Popup-->  
  
 