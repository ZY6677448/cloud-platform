 <template>
 <div>
   <AggreModelAddDialog :aggremodel="aggreModelDialogData.data"
                         :title="aggreModelDialogData.title"
                         :usedScenes="usedScenes"
                           @saveAggreModel="saveAggreModel"></AggreModelAddDialog>
   <el-row type="flex">
     <el-button-group >
  <el-button type="primary" icon="el-icon-plus" @click="openDialog">新增</el-button>
   <!-- <el-button type="primary" icon="el-icon-minus">批量删除</el-button> -->
</el-button-group>
   </el-row>
   <el-row>
            <el-table
      :data="tableData"
      style="width: 100%">
      <el-table-column
        align='left'
        prop="id"
        label="ID"
        width="150">
      </el-table-column>
      <el-table-column
      align='left'
        prop="scene"
        label="应用场景"
         width="300">
      </el-table-column>
      <el-table-column
      label="操作"
      align='left'
      fixed="right"
      >
      <template slot-scope="scope">
        <el-button type="text" size="small" @click="detail(scope.row)">算法配置</el-button>
        <el-button type="text" size="small" @click="deleteAggreModel(scope.row)">删除</el-button>
      </template>
    </el-table-column>
    </el-table>
   </el-row>
 </div>
</template>
<script>
import { mapState } from "vuex";
import AggreModelAddDialog from "@/components/algorithmComp/AggreModelAddDialog.vue";
import { modifyRule, getAllAggreModel,deleteAggreModelByid} from "@/api/getData";
import copy from "@/utils/copy"
export default {
  name: "AlgorithmTable",
  data() {
    return {
      tableData: [],
      aggreModelDialogData: {
        title: "",
        data: {}
      },
      usedScenes:[]
    };
  },
  methods: {
    openDialog() {
      this.aggreModelDialogData.title = "新增应用场景";
      this.aggreModelDialogData.data = {};
      this.$store.commit("openAMAddDialog");
    },
    editCol(row) {
      this.aggreModelDialogData.title = "修改应用场景";
      this.aggreModelDialogData.data = copy(row);
      this.$store.commit("openAMAddDialog");
    },
    saveAggreModel(target) {
      let index = this.tableData.findIndex(rule => rule.id == target.id);
      if (index != -1) {
         this.tableData[index].scene = target.scene;
      }else{
        this.tableData.push(target);
      }
    },
    async deleteAggreModel(row){
      let params = {id : row.id};
      const response = await deleteAggreModelByid(params);
      if (response.success) {
        this.tableData.splice(this.tableData.findIndex(rule => rule.id == row.id), 1);
      } else {
        this.$message("删除应用场景失败");
      }
    },
    async initTableData() {
      const response = await getAllAggreModel();
    
      if (response.success) {
        this.tableData = response.result;
        this.usedScenes = [];
        for(let data of this.tableData){
          this.usedScenes.push(data.scene);
        }
      } else {
        this.$message("获取应用场景失败");
      }
    },
    detail(row){
      this.$router.push('/AlgorithmConfig/'+row.id);
      }
  },
  components: {
    'AggreModelAddDialog': AggreModelAddDialog
  },
  created(){
    this.initTableData();
  }
};
</script>
<style>
.el-row {
  margin-bottom: 20px;
}
</style>
