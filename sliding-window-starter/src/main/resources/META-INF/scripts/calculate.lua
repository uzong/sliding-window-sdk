redis.pcall('zadd', KEYS[1], ARGV[1], ARGV[5]);
redis.pcall('zremrangebyscore', KEYS[1], 0, ARGV[2]);
redis.pcall("expire", KEYS[1], ARGV[3]);
if tonumber(redis.pcall('zcard',KEYS[1])) >= tonumber(ARGV[4]) 
    then return true end;
return false;