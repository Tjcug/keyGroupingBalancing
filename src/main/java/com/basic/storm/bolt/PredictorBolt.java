package com.basic.storm.bolt;

import com.basic.storm.inter.DumpRemoveHandler;
import com.basic.storm.util.PredictorHotKeyUtil;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * locate com.basic.storm.bolt
 * Created by 79875 on 2017/5/8.
 */
public class PredictorBolt extends BaseRichBolt {
    private OutputCollector collector;
    private PredictorHotKeyUtil predictorHotKeyUtil=PredictorHotKeyUtil.getPredictorHotKeyUtilInstance();
    private static Logger logger= LoggerFactory.getLogger(PredictorBolt.class);
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    public void execute(Tuple tuple) {
        final String word = tuple.getStringByField("word");
        Integer count = tuple.getIntegerByField("coincount");

        predictorHotKeyUtil.PredictorHotKey(word,count);

        if(predictorHotKeyUtil.isHotKey(word))
            collector.emit(new Values(word,1));

        predictorHotKeyUtil.SynopsisHashMapAllDump(new DumpRemoveHandler() {
            @Override
            public void dumpRemove(String key) {
                collector.emit(new Values(word,1));
            }
        });

        collector.ack(tuple);
    }

    public void cleanup(){
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word","type"));
    }
}
