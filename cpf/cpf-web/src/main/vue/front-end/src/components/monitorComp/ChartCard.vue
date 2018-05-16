<template>
  <div>
      

    <el-card  :body-style="{ padding: '0px'}">
       <div  :id="id"  :ref="id" class="box-card"  align="cneter"></div>  
    </el-card>
  </div>
</template>
<script>
import { getChartData } from "@/api/getData";
import { formatDate } from "@/utils/date";

export default {
  data() {
    return {};
  },
  mounted() {
    this.initChart();
  },
  props: {
    id: "",
    table: "",
    host: "",
    col: "",
    startTime:'',
    endTime:'',
    text:''
  },
  methods: {
    async initChart() {
      var data = [];
      var date = [];
      var params = {
        host: this.host,
        table: this.table,
        col: this.col,
        startTime: this.startTime,
        endTime: this.endTime
      };
      var response = await getChartData(params);
      if (response.success) {
        for (let time of response.result[0]) {
          date.push(formatDate(new Date(new Number(time))));
        }
        for(let d of response.result[1]){
          data.push(JSON.stringify(new Number(d)));
        }
      }
      var option = {
        tooltip: {
          trigger: "axis",
          position: function(pt) {
            return [pt[0], "10%"];
          }
        },
        title: {
          left: "center",
          text: this.text
        },
        toolbox: {
          feature: {
            dataZoom: {
              yAxisIndex: "none"
            },
            restore: {},
            saveAsImage: {}
          }
        },
        xAxis: {
          type: "category",
          boundaryGap: false,
          data: date
        },
        yAxis: {
          type: "value",
          boundaryGap: [0, "100%"]
        },
        dataZoom: [
          {
            type: "inside",
            start: 0,
            end: 10
          },
          {
            start: 0,
            end: 10,
            handleIcon:
              "M10.7,11.9v-1.3H9.3v1.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4v1.3h1.3v-1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9z M13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z",
            handleSize: "80%",
            handleStyle: {
              color: "#fff",
              shadowBlur: 3,
              shadowColor: "rgba(0, 0, 0, 0.6)",
              shadowOffsetX: 2,
              shadowOffsetY: 2
            }
          }
        ],
        series: [
          {
            name: "数据",
            type: "line",
            smooth: true,
            symbol: "none",
            sampling: "average",
            itemStyle: {
              normal: {
                color: "rgb(255, 70, 131)"
              }
            },
            areaStyle: {
              normal: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  {
                    offset: 0,
                    color: "rgb(255, 158, 68)"
                  },
                  {
                    offset: 1,
                    color: "rgb(255, 70, 131)"
                  }
                ])
              }
            },
            data: data
          }
        ]
      };
      let chart = echarts.init(this.$refs[this.id]);
      chart.setOption(option);
    }
  }
};
</script>
<style>
.box-card {
  width: 700px;
  height: 400px;
}
</style>