select sum(sus) * 2.5 as sus_score
from (
		select avg(value) as sus
	  from answers a join questions q on q.id = question_id
    where q.id between 1 and 10 
		group by q.id) as inner_query
  
  

