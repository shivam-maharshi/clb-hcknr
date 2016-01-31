
// video query string = <?v=blablabla>
var video_id = window.location.search.split('v=')[1];
var ampersandPosition = video_id.indexOf('&');
if(ampersandPosition != -1) {
  video_id = video_id.substring(0, ampersandPosition);
}


var loadInfo = function (video_id) {
    var gdata = document.createElement("script");
    gdata.src = "https://gdata.youtube.com/feeds/api/videos/" + video_id + "?v=2&alt=jsonc&callback=storeInfo";
    var body = document.getElementsByTagName("body")[0];
    body.appendChild(gdata);
};

var storeInfo = function (info) {
    console.log(info.data.title);
};

var interval = 15;
var player;
// creates empty image next to the video
function createAddImage() {
	var container = document.getElementById('placeholder-playlist');
player = player || document.getElementById('movie_player'); 
	container.style.display = 'block';
	container.innerHTML='<style>'+
		'.div1 {'+
			'text-align: center;'+
			'width: 400px;'+
			    'height: 80px;'+
			    'border: 1px solid blue;'+
			    'box-sizing: border-box;}'+
		'.div2 {'+
			'text-align: center;'+
			'color:black;'+
			    'width: 400px;'+
			    'height: 250px;    '+
			    'padding: 50px;'+
			    'border: 1px solid blue;'+
			    'box-sizing: border-box;}'+
		'</style>'+
		'<div class="div1" id="div1">'+
		'<img src = "http://s29.postimg.org/pmzf9z7mf/twitterlogocroppedandresized.png">'+
		'<p id="demo"></p>'+
		'</div>'+
		'<div class="div2" id="div2" >'+
		'</div>';
	  var xhr = new XMLHttpRequest();
 	    xhr.open('GET', 'http://10.128.128.142:8080/snip/data?v=EgqUJOudrcM');
 	   xhr.responseType = 'json';

	var text = xhr.response;
	var obj = JSON.parse(text);
	
	var x;
	for(x in obj.twitter){
	   var comment1= obj.twitter[x].name + " " + obj.twitter[x].tweet;
	    var newParagraph1 = document.createElement('p');
	    newParagraph1.textContent = comment1;
	    document.getElementById("div2").appendChild(newParagraph1);
	  
}

	    var newParagraph2 = document.createElement('p');
	    newParagraph2.textContent = video_id;
	    document.getElementById("div2").appendChild(newParagraph2);

	var newParagraph3 = document.createElement('p');
	    newParagraph3.textContent = player.getVideoData().title;
	    document.getElementById("div2").appendChild(newParagraph3);
}
// executes every <interval> seconds
setInterval(function() {
	player = player || document.getElementById('movie_player'); // youtube player object
	var start = Math.floor(player.getCurrentTime()); // current time of the video in seconds
	var end = start + Math.min(interval, Math.floor(player.getDuration() - start)); // current time + <interval>
	// UNCOMMENT ONCE THERE IS SERVER RUNNING
	// var xhr = new XMLHttpRequest();
 //    xhr.open('GET', 'https://localhost/getAd' + videoId + '&start=' + start + '&end=' + end);
 //    xhr.responseType = 'text';
 //    xhr.onload = function() {
 //    	if (!document.getElementById('main_add_image'))
 //    		createAddImage();
 //    	document.getElementById('main_add_image').src = xhr.response + '?' + Math.random();
 //    };
 //    xhr.send(null);
    if (!document.getElementById('main_add_image'))
    	createAddImage();
    // refreshes image's src
  //  document.getElementById('main_add_image').src = "http://www.lakeviewcommunitychurch.net/wp-content/uploads/2012/12/new-twitter-logo.jpg";
   // document.getElementById('main_add_image2').src = "https://www.facebookbrand.com/img/fb-art.jpg";
}, interval * 100);

