package models

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession
import scala.slick.session.Database
import play.api.db.DB
import play.api.Play.current

case class Motorcycle(id: Option[Long], make: String,
  model: String, engineCapacity: Int)

object Motorcycles extends Table[Motorcycle]("motorcycles") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def make = column[String]("make")
  def model = column[String]("model")
  def engineCapacity = column[Int]("enginecapacity")
  // the ? is for Option
  def * = id.? ~ make ~ model ~ engineCapacity <> (Motorcycle, Motorcycle.unapply _)

}

object MotorcycleDAO {
   Database.forDataSource(DB.getDataSource()) withSession {
     Motorcycles.ddl.create
   }
   
  def getAll: List[Motorcycle] = {    
    Database.forDataSource(DB.getDataSource()) withSession {       
      Query(Motorcycles).list
    }
  }

  def insert(motorcycle: Motorcycle): Unit = {
    Database.forDataSource(DB.getDataSource()) withSession {
      Motorcycles.insert(motorcycle);
    }
  }

  def update(id: Long, motorcycle: Motorcycle): Unit = {
    Database.forDataSource(DB.getDataSource()) withSession {
      val exisitingBike = for { bike <- Motorcycles if bike.id === id } yield bike
      exisitingBike.update(motorcycle)

    }

  }

  def findByMakeAndModel(motorcycle: Motorcycle): Option[Motorcycle] = {
    Database.forDataSource(DB.getDataSource()) withSession {
      val exisitingBike = for {
        bike <- Motorcycles
        if bike.make === motorcycle.make
        if bike.model === motorcycle.model
      } yield bike

      exisitingBike.list.headOption
    }
  }
  
   def delete(id: Long) = {
        Database.forDataSource(DB.getDataSource()) withSession {
      val exisitingBike = for { bike <- Motorcycles if bike.id === id } yield bike
           exisitingBike.delete
        }
   }
  
   def findById(id: Long):Option[Motorcycle] = {
        Database.forDataSource(DB.getDataSource()) withSession {
           val exisitingBike = for { bike <- Motorcycles if bike.id === id } yield bike
           exisitingBike.list.headOption
        }
   }
  

}