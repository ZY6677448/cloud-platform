<template> 
   <div > 
    <div> 
     <el-row> 
      <el-breadcrumb separator="/"> 
       <el-breadcrumb-item>
        云平台
       </el-breadcrumb-item> 
       <el-breadcrumb-item>
        监控
       </el-breadcrumb-item> 
       <el-breadcrumb-item>
        cpu监控
       </el-breadcrumb-item> 
      </el-breadcrumb> 
     </el-row>
     <el-row>
       <el-form :inline="true" align="left">
         <el-form-item label="日期范围">
                        <el-date-picker
      v-model="time"
      type="datetimerange"
      placeholder="选择日期时间"
      value-format="yyyy-MM-dd HH:mm:ss">
    </el-date-picker>
         </el-form-item>
         <el-form-item>
             <el-button @click="search">查询</el-button>
         </el-form-item>
       </el-form>
     </el-row>
     <el-row :gutter="20">
       <el-col :span="12">
         <ChartCard :id="chartId1" table='win_cpu'
          :host="this.$route.params.hostname"
           col='Percent_Interrupt_Time'
           text='cpu利用率'
          :startTime='start'
          ref='cpuChart'
          :endTime='end'></ChartCard>
       </el-col>
       <el-col :span="12">
         <ChartCard :id="chartId1" table='win_mem'
          :host="this.$route.params.hostname"
           col='Available_Bytes'
           text='内存利用率(MB)'
          :startTime='start'
          ref='memChart'
          :endTime='end'></ChartCard>
       </el-col>       
     </el-row>
     <el-row :gutter="20" v-for="(datas,index) in AVGData" :key="index"> 
      <el-col :span="8" v-for="(data) in datas" :key="data.type">
        <AVGChard :content="data"></AVGChard>
      </el-col> 
     </el-row> 
    </div> 
   </div> 
  </template> 
<script>
import { getAVGData, getChartData } from "@/api/getData";
import AVGChard from "@/components/monitorComp/AVGChard.vue";
import ChartCard from "@/components/monitorComp/ChartCard.vue";
import search from "@/components/monitorComp/search.vue";
import { formatDate } from "@/utils/date";
export default {
  name: "monitorChart",
  data() {
    return {
      chartId1: "sad",
      chartId2: "gad",
      AVGData: [],
      time:  [],
      cpuChartData: []
    };
  },
  methods: {
    async initData(start, end) {
      var params = {
        host: this.$route.params.hostname,
        startTime: start,
        endTime: end
      };
      var response = await getAVGData(params);
      if (response.success) {
        //将数组切分，每份三个数据
        this.AVGData = [];
        var size;
        var index = 0;
        if (response.result.length % 3 == 0) {
          size = response.result.length / 3;
        } else {
          size = response.result.length / 3 + 1;
        }
        for (let i = 0; i < size; i++) {
          let tempData = [];
          for (let j = 0; j < 3; j++) {
            if(index < response.result.length){
            tempData.push(response.result[index++]);
            }
          }
          this.AVGData.push(tempData);
        }
      }
    },
    search() {
      if (this.time != "") {
        this.initData(this.start, this.end);
        this.$refs.cpuChart.initChart();
        this.$refs.memChart.initChart();

      }
    }
  },
  components: {
    ChartCard: ChartCard,
    search: search,
    AVGChard: AVGChard
  },
  computed: {
    start() {
      if (this.time != "") {
        return this.time[0];
      }
    },
    end() {
      if (this.time != "") {
        return this.time[1];
      }
    }
  },
  created() {
    var hour = 60*60*1000;
    this.time = [];
    var endTime = new Date();
    var startTime = new Date(endTime.getTime()-hour);
    endTime = formatDate(endTime);
    startTime = formatDate(startTime);
    this.time.push(startTime,endTime);
    this.initData(startTime,startTime);
  }
};
</script>
<style>
.el-row {
  margin-bottom: 20px;
}
</style>
