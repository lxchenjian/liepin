SELECT 
	id,hr_id,company_id,job_name,job_type,exp_years,edu,begin_salary,end_salary,monthly_salary,job_desc,tags,city,address,`status`,create_time,updated_time 
FROM 
	job 
WHERE (id = '1547454147718361090' AND hr_id = '123' AND company_id = '1541300495639175169' AND `status` IN (1,2,3))