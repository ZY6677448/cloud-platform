<template>
  <div>
 <algorithm-add-dialog @addModel="addModel" :usedNames="usedNames"></algorithm-add-dialog>

  <el-row type="flex">
      <el-button-group >
        <el-button type="primary" icon="el-icon-plus" @click="openDialog">新增</el-button>
      </el-button-group>
   </el-row>
    <el-row >
      <el-collapse >
        <el-collapse-item v-for="model in aggreModel.models" :key="model.id" :title="calTitle(model.name,model.weight,model.correctRate)" >
          <AlgorithmEdit :model="model"  @deleteModel="deleteModel"></AlgorithmEdit>
        </el-collapse-item>
      </el-collapse>
    </el-row>
  </div>
</template>
<script>
import { mapState } from "vuex";
import AlgorithmEdit from "@/components/algorithmComp/AlgorithmEdit.vue";
import AlgorithmAddDialog from "@/components/algorithmComp/AlgorithmAddDialog.vue";
import { getAggreModel } from "@/api/getData";
export default {
  name: "AlgotithmDetail",
  data() {
    return {
      aggreModel: {
        models: []
      }
    };
  },
  computed:{
    usedNames(){
        var usedNames= [];
        for (let model of this.aggreModel.models) {
          usedNames.push(model.name);
        }
        return usedNames;
    }
  },
  methods: {
    calTitle(name, weight,correctRate) {
      return name + "   权重：" + weight + "  正确率: " + correctRate;
    },
    openDialog() {
      this.$store.commit("openAlgAddDialog");
    },
    addModel(model) {
      this.initModels();
    },
    deleteModel(id) {
      let models = this.aggreModel.models;
      models.splice(models.findIndex(model => model.id == id), 1);
    },
    async initModels() {
      let params = { id: this.$route.params.id };
      const response = await getAggreModel(params);
      if (response.success) {
        this.aggreModel = response.result;

      } else {
        this.$message("获取模型失败");
      }
    }
  },
  components: {
    AlgorithmEdit: AlgorithmEdit,
    AlgorithmAddDialog: AlgorithmAddDialog
  },
  created() {
    this.initModels();
  }
};
</script>
