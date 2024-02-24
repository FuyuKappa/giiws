import fs from "fs";
import { parse } from "csv-parse";

async function readCSV(path, out){
	// Create a readstream
	// Parse options: delimiter and start from line 1
	fs.createReadStream(path)
	  .pipe(parse({ delimiter: ",", from_line: 1 }))
	  .on("data", function (row) {
		// executed for each row of data
		out.push(row);
	  })
	  .on("end", function () {
		// executed when parsing is complete
		console.log("File read successful: " + path);
	  });
};

const controller ={
	getGraphic: async function(req,res){
		// specify the path of the CSV file
		let ver = req.params['ver'];
		let out = [];
		
		const path = "./public/data/"+ ver +".csv";

		if(fs.existsSync(path)){
			await readCSV(path, out);
			//move it to here
		}
		else{
			console.log("File not found at " + path); 
			//Put an error page here
			//res.redirect('/error');
			//or res.status(404)?
		}
		//move this to the if-else blocks
		res.render("body", {layout:false,"script":"../js/load.js", "style":"../styles/styles.css", data: out, ver: ver});
	},
	getIndex: async function(req,res){
		res.render("home", {layout:false,"style": "../styles/styles.css"})
	},
};

export default controller;