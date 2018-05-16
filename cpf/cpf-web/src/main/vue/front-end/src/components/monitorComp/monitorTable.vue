 <template>
 <div>
   <el-row type="flex">
             <el-breadcrumb separator="/"> 
       <el-breadcrumb-item>云平台</el-breadcrumb-item> 
       <el-breadcrumb-item>监控</el-breadcrumb-item> 
       <el-breadcrumb-item>设备监控</el-breadcrumb-item> 
      </el-breadcrumb> 
   </el-row>
   <el-row >
     <el-col :span="5">
       <el-input v-model="ipaddr" placeholder="请输入IP地址">
                 <el-button slot="append" icon="el-icon-search" @click="initTableData"></el-button>
       </el-input>
     </el-col>
   </el-row>
   <el-row>
            <el-table
       :data="tableData"
      style="width: 100%">
      <el-table-column
        align='left'
        prop="id"
        label="id"
        width="350">
      </el-table-column>
      <el-table-column
      align='left'
        prop="aid"
        label="aid"
        width="350">
      </el-table-column>
            <el-table-column
      align='left'
        prop="ipaddr"
        label="ip"
        width="300">
      </el-table-column>
      <el-table-column
      align='left'
        prop="hostname"
        label="hostname"
         width="300">
      </el-table-column>
      <el-table-column
      label="操作"
      align='left'
      fixed="right"
      >
      <template slot-scope="scope">
        <el-button type="text" size="small" @click="detail(scope.row)">详情</el-button>
      </template>
    </el-table-column>
    </el-table>
   </el-row>
   <el-row>
         <el-pagination  
         background
    layout="total,prev, pager, next,sizes"
    :page-size="pagesize"
    :page-sizes="[10, 20, 50, 100]"
    :current-page="currentPage"
    @current-change="handleCurrentChange"
    @size-change="handleSizeChange"
    :total="total">
  </el-pagination>
   </el-row>
 </div>
</template>
<script>
import { getAssets } from "@/api/getData";
import search from "@/components/monitorComp/search.vue";

export default {
  name: "monitorTable",
  data() {
    return {
      tableData: [],
      total:0,
      currentPage:0,
      pagesize:10,
      ipaddr:'',
      allData:[]
    };
  },
  methods: {
    async initTableData() {
      let params = {"page":this.currentPage-1,"size":this.pagesize,"ipaddr":this.ipaddr};
      const response = await getAssets(params);
      if (response.success) {
        this.total = response.result.totalElements;
        this.tableData =  response.result.content;
      } else {
        this.$message("获取监控数据失败");
      }
    },
    detail(row) {
      this.$router.push("/Monitor/" + row.hostname);
    },
    handleSizeChange(size){
       this.pagesize = size;
       this.initTableData();
    },
    handleCurrentChange(currentPage){
       this.currentPage = currentPage;
       this.initTableData();
    }
  },
  created() {
    this.initTableData();
  },
  components: {
    search: search
  }
};
</script>
<style scoped>
.el-row {
  margin-bottom: 20px;
}
</style>
