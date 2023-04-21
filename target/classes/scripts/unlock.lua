--- redis API redis.call()
--- KEYS[]数组存储redis的key参数,ARGV[]数组存储redis的value参数 下标从1开始
--- 取出锁中的线程标识
local threadId = redis.call("get",KEYS[1]);
if (threadId == ARGV[1]) then
    return redis.call('del',KEYS[1]);
end
--- 不一致直接返回
return 0