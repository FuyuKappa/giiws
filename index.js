import express from "express";
import exphbs from "express-handlebars";
import bodyParser from 'body-parser';
import path from 'path';
import { config } from 'dotenv';
import routes from './routes.js';
config();

const app = express();

//session code for handlebars
app.use(function(req,res, next){
	res.locals.session = req.session;
	//console.log(res.locals.session);
	next();
});

const port = process.env.SERVER_PORT;

import { fileURLToPath }        from 'url';
import { dirname, join }        from 'path';

const hbs = exphbs.create({ //A new handlebar extension instantiation that includes helpers
    extname: 'hbs',
    helpers:{
        eq: function(a,b){ //this helper is used to compare to values in a handle bar to use: {{#if (eq value1 value2)}}
            return a==b;
        },
        multiply: function(a, b) {return (a * b).toFixed(2);},
		slice: function(array, start, end){
			if(end !=  '-'){
				return array.slice(start,end);
			}
			console.log(array.slice(start));
			return array.slice(start);
		},
		isBlank: function(a){
			return a != '-';
		},
		verFormat: function(a){
			let b = a.split('_');
			return b[0] + "." + b[1];
		},
		getGold: function(array){
			let gold = [];
			for (const item of array){
				if(item[4] != '-'){
					gold.push(item);
				}
			};
			return gold;
		},
		getGrey: function(array){
			let grey = [];
			for (const item of array){
				if(item[4] == '-'){
					grey.push(item);
				}
			};
			let sorted = grey.sort(function(a,b) { return b[1] - a[1]; });
			return sorted;
		},
		sliceGrey: function(array, option){
			let overflow = array.length % 6;
			option = option.toLowerCase();
			if(overflow == 0 && option == "first"){
				return array;
			}			
			else if(option == "first"){
				return array.slice(0,-overflow)
			}
			else if(overflow != 0 && option == "last"){
				return array.slice(-overflow)
			}
			
		},
		
    }
})

app.engine("hbs", hbs.engine);
app.set("view engine", "hbs");
//app.set("views", "./views");

app.use (express.static(`public`) );
app.use ( express.urlencoded({ extended: true }));
app.use ( bodyParser.urlencoded({ extended: true }));
app.use ( bodyParser.json() );
app.use ( express.json() );


app.use(`/`, routes); 

app.listen(port, function () {
    console.log(`Server is running at:`);
    console.log(`http://localhost:` + port);
});


