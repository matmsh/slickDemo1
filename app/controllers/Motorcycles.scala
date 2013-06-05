package controllers

import models.Motorcycle
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.Result
import models.MotorcycleDAO



object Motorcycles extends Controller{

    
   lazy val motorcycleMapping=mapping(
        // no binding for id.
        "id" -> optional(longNumber) ,
        "make" -> nonEmptyText,
        "model" -> nonEmptyText,
        "engineCapacity" ->  number(50,Int.MaxValue, false)
        )(Motorcycle.apply)(Motorcycle.unapply)
         
 
   val motorcycleForm = Form[Motorcycle](motorcycleMapping)  
  
   
   def list = Action {
       val bikes = MotorcycleDAO.getAll;
       Ok(views.html.list(MotorcycleDAO.getAll))
   }
   
   def newMotorcycle = Action{
      Ok(views.html.add(motorcycleForm))
   } 
   
   def create = Action {
     implicit request =>
        val motorcycleFormNew = this.motorcycleForm.bindFromRequest;
        motorcycleFormNew.fold(
          hasErrors = {formWithError =>
                     Ok(views.html.add(formWithError)) },       
          success = validateCreate
       )
     
   }

  private def validateCreate(motorcycle: Motorcycle): Result = {
    // When adding a new motorcycle, it must not already exists in the db.     
    if (exists(motorcycle)) {
      val newErrorForm = motorcycleForm.fill(motorcycle).withGlobalError("Make and model already exist.")
      Ok(views.html.add(newErrorForm))

    } else {
      // Passed all validation. Add motorcycle to db.
      MotorcycleDAO.insert(motorcycle);
      Redirect(routes.Motorcycles.list)
    }

  }
   
   def editMotorcycle(id:Long)= Action{
      implicit request =>
       val motorcycleOption = MotorcycleDAO.findById(id)
        val bike = motorcycleForm.fill(motorcycleOption.get) 
         // Put id of selected motorcycle in session. 
        Ok(views.html.edit(bike)).withSession( session + ("motorcycleId" -> id.toString))
   }
   
   def update =  Action {
     implicit request =>
        val motorcycleFormNew = this.motorcycleForm.bindFromRequest;
        motorcycleFormNew.fold(
          hasErrors = {formWithError =>
                     Ok(views.html.edit(formWithError)) },     
                     
          success = {  motorcycle => { val id = session.get("motorcycleId").get.toLong;
                                       MotorcycleDAO.update(id,motorcycle);
                                       // Remove id from session. 
                                       session - "id";
                                       Redirect(routes.Motorcycles.list) }
          }
       )           
   }
   
    def delete(id:Long)=Action{       
         MotorcycleDAO.delete(id); 
         Redirect(routes.Motorcycles.list)
    }
   
   
   private def exists(motorcycle:Motorcycle):Boolean ={
      MotorcycleDAO.findByMakeAndModel(motorcycle) != None      
   }
}