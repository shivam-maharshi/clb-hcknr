function myFunction(){
	var cars = ["Saab", "Volvo", "BMW"];
	var x;
	for (x in cars) {
	   var comment = cars[x];
           var newParagraph = document.createElement('p');
 	   newParagraph.textContent = comment;
 	   document.getElementById("div1").appendChild(newParagraph);
	}
}
