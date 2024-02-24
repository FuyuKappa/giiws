import { Router } from "express";
import controller from './controllers.js';
import bodyParser from 'body-parser';

const router = Router();

//BOILERPLATE
router.use(bodyParser.urlencoded({ extended: true }));
router.use(bodyParser.json());

//GETS
router.get(`/favicon.ico`, (req,res)=>res.status(204).end());
router.get(`/:ver`, controller.getGraphic);
router.get(`/`, controller.getIndex);

//router.get(`/`, controller.getIndex);

//  user handling
//router.get(`/login`, controller.getLogin);
//router.get('/register', controller.getRegister);


//POSTS
//router.post('/statusChange', controller.statusChange);

export default router;
