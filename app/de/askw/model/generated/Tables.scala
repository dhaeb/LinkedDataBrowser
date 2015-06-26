package de.askw.model.generated

// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = scala.slick.driver.MySQLDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: scala.slick.driver.JdbcProfile
  import profile.simple._
  import scala.slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import scala.slick.jdbc.{GetResult => GR}
  
  /** DDL for all tables. Call .create to execute. */
  lazy val ddl = Answers.ddl ++ Questions.ddl ++ SusAnswer.ddl
  
  /** Entity class storing rows of table Answers
   *  @param questionId Database column question_id DBType(INT), Default(0)
   *  @param susAnswerId Database column sus_answer_id DBType(INT), Default(0)
   *  @param value Database column value DBType(TINYINT) */
  case class AnswersRow(questionId: Int = 0, susAnswerId: Int = 0, value: Byte)
  /** GetResult implicit for fetching AnswersRow objects using plain SQL queries */
  implicit def GetResultAnswersRow(implicit e0: GR[Int], e1: GR[Byte]): GR[AnswersRow] = GR{
    prs => import prs._
    AnswersRow.tupled((<<[Int], <<[Int], <<[Byte]))
  }
  /** Table description of table answers. Objects of this class serve as prototypes for rows in queries. */
  class Answers(_tableTag: Tag) extends Table[AnswersRow](_tableTag, "answers") {
    def * = (questionId, susAnswerId, value) <> (AnswersRow.tupled, AnswersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (questionId.?, susAnswerId.?, value.?).shaped.<>({r=>import r._; _1.map(_=> AnswersRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column question_id DBType(INT), Default(0) */
    val questionId: Column[Int] = column[Int]("question_id", O.Default(0))
    /** Database column sus_answer_id DBType(INT), Default(0) */
    val susAnswerId: Column[Int] = column[Int]("sus_answer_id", O.Default(0))
    /** Database column value DBType(TINYINT) */
    val value: Column[Byte] = column[Byte]("value")
    
    /** Primary key of Answers (database name answers_PK) */
    val pk = primaryKey("answers_PK", (questionId, susAnswerId))
    
    /** Foreign key referencing Questions (database name answers_ibfk_1) */
    lazy val questionsFk = foreignKey("answers_ibfk_1", questionId, Questions)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing SusAnswer (database name answers_ibfk_2) */
    lazy val susAnswerFk = foreignKey("answers_ibfk_2", susAnswerId, SusAnswer)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Answers */
  lazy val Answers = new TableQuery(tag => new Answers(tag))
  
  /** Entity class storing rows of table Questions
   *  @param id Database column id DBType(INT), AutoInc, PrimaryKey
   *  @param question Database column question DBType(VARCHAR), Length(255,true), Default(None) */
  case class QuestionsRow(id: Int, question: Option[String] = None)
  /** GetResult implicit for fetching QuestionsRow objects using plain SQL queries */
  implicit def GetResultQuestionsRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[QuestionsRow] = GR{
    prs => import prs._
    QuestionsRow.tupled((<<[Int], <<?[String]))
  }
  /** Table description of table questions. Objects of this class serve as prototypes for rows in queries. */
  class Questions(_tableTag: Tag) extends Table[QuestionsRow](_tableTag, "questions") {
    def * = (id, question) <> (QuestionsRow.tupled, QuestionsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, question).shaped.<>({r=>import r._; _1.map(_=> QuestionsRow.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id DBType(INT), AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column question DBType(VARCHAR), Length(255,true), Default(None) */
    val question: Column[Option[String]] = column[Option[String]]("question", O.Length(255,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table Questions */
  lazy val Questions = new TableQuery(tag => new Questions(tag))
  
  /** Entity class storing rows of table SusAnswer
   *  @param id Database column id DBType(INT), AutoInc, PrimaryKey
   *  @param comment Database column comment DBType(VARCHAR), Length(255,true), Default(None)
   *  @param language Database column language DBType(VARCHAR), Length(255,true), Default(None) */
  case class SusAnswerRow(id: Int, comment: Option[String] = None, language: Option[String] = None)
  /** GetResult implicit for fetching SusAnswerRow objects using plain SQL queries */
  implicit def GetResultSusAnswerRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[SusAnswerRow] = GR{
    prs => import prs._
    SusAnswerRow.tupled((<<[Int], <<?[String], <<?[String]))
  }
  /** Table description of table sus_answer. Objects of this class serve as prototypes for rows in queries. */
  class SusAnswer(_tableTag: Tag) extends Table[SusAnswerRow](_tableTag, "sus_answer") {
    def * = (id, comment, language) <> (SusAnswerRow.tupled, SusAnswerRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, comment, language).shaped.<>({r=>import r._; _1.map(_=> SusAnswerRow.tupled((_1.get, _2, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id DBType(INT), AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column comment DBType(VARCHAR), Length(255,true), Default(None) */
    val comment: Column[Option[String]] = column[Option[String]]("comment", O.Length(255,varying=true), O.Default(None))
    /** Database column language DBType(VARCHAR), Length(255,true), Default(None) */
    val language: Column[Option[String]] = column[Option[String]]("language", O.Length(255,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table SusAnswer */
  lazy val SusAnswer = new TableQuery(tag => new SusAnswer(tag))
}