package de.aksw

import de.askw.model.generated.Tables
import de.askw.model.generated.Tables.{QuestionsRow, SusAnswerRow}
import org.scalatest.FunSuite
import scala.slick.driver.MySQLDriver.simple._

/**
 * Created by dhaeb on 25.06.15.
 */
class TestSlickDbMapping extends FunSuite {

  test("test db connection"){
    assume(isReachable("kdi-student.de"))
    Database.forURL("jdbc:mysql://kdi-student.de:3306/ldb", driver="com.mysql.jdbc.Driver", user="ldb", password="sus").withSession { implicit session =>
      val column = Tables.Questions.length.run
      assert(column == 13)
      ()
    }
  }
}
