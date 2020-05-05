-- Feel free to make changes to this query based on what you want to see from
-- the benchmark database

.print ' '
.print 'Insert performance ranks for banner id - top 10 inserts'

with workload as
    (select * from benchmarks where workload='insert')
select (
    select count(*)
    from workload as t2
    where t2.performance <= t1.performance
) as position, * 
from workload as t1
where banner='B0123456789'
order by position asc
limit 10;

.print ' '
.print 'Top 10 performers for insert'

select *
from (select * from benchmarks where workload='insert')
order by performance asc
limit 10;
