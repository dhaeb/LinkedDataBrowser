select sum(weighted_sus) / (select sum(avgWeighting)
								   from (select avg(value) avgWeighting
	    												 FROM answers  
												   where answers.question_id between 11 and 13
																GROUP BY answers.sus_answer_id) as avgWeightingTable) as weighted_sus_total
from (
		select sum(value) * 2.5 * (select avg(value)
																							      FROM answers  
																															where a.sus_answer_id = answers.sus_answer_id and question_id between 11 and 13) as weighted_sus
	  from answers a 
    where a.question_id between 1 and 10
		group by a.sus_answer_id) as sub
  
  






