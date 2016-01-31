
// video query string = <?v=blablabla>
$(document).ready(function(){


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

    function getSnipData() {
        var container = document.getElementById('placeholder-playlist');
        container.style.display = 'block';
        container.innerHTML='<style>'+
            '.div2.p { margin: 0.5em}'+
            '.div4.p { margin: 0.5em}'+
            '.div1 {'+
            'text-align: left;'+
            'width: 400px;'+
            'height: 80px;'+
            'border: 1px solid blue;'+
            'box-sizing: border-box;}'+
            '.div2 {'+
            'text-align: left;'+
            'color:black;'+
            'width: 400px;'+
            'height: 180px;    '+
            'padding: 10px;'+
            'border: 1px solid blue;'+
            'box-sizing: border-box;}'+
	    '.div3 {'+
            'text-align: left;'+
            'width: 400px;'+
            'height: 80px;'+
            'border: 1px solid red;'+
            'box-sizing: border-box;}'+
            '.div4 {'+
            'text-align: left;'+
            'color:black;'+
            'width: 400px;'+
            'height: 180px;    '+
            'padding: 10px;'+
            'border: 1px solid red;'+
            'box-sizing: border-box;}'+
            '</style>'+
            '<div class="div1" id="div1">'+
            '<img src = "http://s29.postimg.org/pmzf9z7mf/twitterlogocroppedandresized.png">'+
            '<p id="demo"></p>'+
            '</div>'+
            '<div class="div2" id="div2" >'+
            '</div>'+
            '<div class="div3" id="div3">'+
            '<img src = "http://web.ccpgamescdn.com/newssystem/media/65486/1/REDDIT.png" height=79 width=396>'+
            '<p id="demo2"></p>'+
            '</div>'+
            '<div class="div4" id="div4" >'+
            '</div>';

        //Uncomment to get data from Server
      //  $.ajax({
        //    type: "GET",
        //    url: "http://10.128.128.142:8080/snip/data?v=EgqUJOudrcM",
        //    success: function(response) {
        //        if (response && response.twitter) {
         //           populateTweets(response.twitter);
         //       }

           // },
         //   error: function() {
      //          console.log('Error!!')
       //     }
      //  });
    console.log(data);
       populateTweets(data.twitter);
populateRed(data.reddit)


        function populateTweets(tweets) {
            for(var x in tweets) {
                var tweet = $('<p>').html(tweets[x].name + ': ' + tweets[x].tweet);
                $('#div2').append(tweet);
            }

        }
function populateRed(reddit){
            for(var x in reddit) {
                var titles = $('<p>').html(reddit[x].name + ': ' + reddit[x].title);
                $('#div4').append(titles);
            }

        }
    }
    setInterval(getSnipData, interval * 100);
});

var data = {
    "twitter": [
        {
            "name": "@drsamirbhatta",
            "tweet": "https://t.co/PZVJO7LHyj How deep is your love...",
            "date": "Sat Jan 30 21:42:08 EST 2016"
        },
        {
            "name": "@banksxxcm",
            "tweet": "I liked a @YouTube video https://t.co/ciT3mvb3KG Calvin Harris & Disciples - How Deep Is Your Love",
            "date": "Sat Jan 30 21:37:01 EST 2016"
        },
        {
            "name": "@charlenehildeb1",
            "tweet": "Calvin Harris & Disciples - How Deep Is Your Love https://t.co/Qcksd6Mixx via @YouTube",
            "date": "Sat Jan 30 21:34:10 EST 2016"
        },
        {
            "name": "@Charjunsan",
            "tweet": "Calvin Harris & Disciples - How Deep Is Your Love https://t.co/jx7IWPrJJZ vía @YouTube",
            "date": "Sat Jan 30 21:09:48 EST 2016"
        },
        {
            "name": "@AmoElRocks2",
            "tweet": "Calvin Harris & Disciples - How Deep Is Your Love https://t.co/TmykEpPRc4 vía @YouTube",
            "date": "Sat Jan 30 20:55:07 EST 2016"
        },
        {
            "name": "@_Trackable",
            "tweet": "I added a video to a @YouTube playlist https://t.co/9uqyk38agS Calvin Harris & Disciples - How Deep Is Your Love",
            "date": "Sat Jan 30 20:32:01 EST 2016"
        }
    ],
    "reddit": [
        {
            "name": "Notonreddit117",
            "title": "I don't know where else to say this to you, but here goes.",
            "date": "Sat Jan 17 14:55:28 EST 1970",
            "link": "/r/love/comments/43cn9n/i_dont_know_where_else_to_say_this_to_you_but/"
        },
        {
            "name": "bladeovcain",
            "title": "Ran into girl that i liked in high school five years ago earlier today. And it turns out she still likes me as well. (very lengthy story)",
            "date": "Sat Jan 17 14:55:32 EST 1970",
            "link": "/r/love/comments/43cv6g/ran_into_girl_that_i_liked_in_high_school_five/"
        },
        {
            "name": "hopelessromantic7",
            "title": "The subtle things",
            "date": "Sat Jan 17 14:56:16 EST 1970",
            "link": "/r/love/comments/43f2ac/the_subtle_things/"
        },
        {
            "name": "kill_em_all90",
            "title": "I love you with every fiber of my being",
            "date": "Sat Jan 17 14:55:26 EST 1970",
            "link": "/r/love/comments/43cjl0/i_love_you_with_every_fiber_of_my_being/"
        },
        null,
        null,
        null,
        null,
        null,
        null
    ]
};









// creates empty image next to the video

// executes every <interval> seconds
//setInterval(function() {
//	player = player || document.getElementById('movie_player'); // youtube player object
//	var start = Math.floor(player.getCurrentTime()); // current time of the video in seconds
//	var end = start + Math.min(interval, Math.floor(player.getDuration() - start)); // current time + <interval>
//	// UNCOMMENT ONCE THERE IS SERVER RUNNING
//	// var xhr = new XMLHttpRequest();
// //    xhr.open('GET', 'https://localhost/getAd' + videoId + '&start=' + start + '&end=' + end);
// //    xhr.responseType = 'text';
// //    xhr.onload = function() {
// //    	if (!document.getElementById('main_add_image'))
// //    		createAddImage();
// //    	document.getElementById('main_add_image').src = xhr.response + '?' + Math.random();
// //    };
// //    xhr.send(null);
//    if (!document.getElementById('main_add_image'))
//    	createAddImage();
//    // refreshes image's src
//  //  document.getElementById('main_add_image').src = "http://www.lakeviewcommunitychurch.net/wp-content/uploads/2012/12/new-twitter-logo.jpg";
//   // document.getElementById('main_add_image2').src = "https://www.facebookbrand.com/img/fb-art.jpg";
//}, interval * 100);

