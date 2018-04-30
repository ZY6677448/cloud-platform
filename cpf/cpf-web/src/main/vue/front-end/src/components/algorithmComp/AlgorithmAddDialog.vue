<template>
  <div>
      <el-dialog title="选择算法模型"  :visible.sync="AlgorithmAddDialogState" width="400px" >
  <el-form :model="form" align="left" :rules="rules" ref="AlgorithmAddForm" >
    <el-form-item label="模型名称" prop="name">
      <el-row>
        <el-col :span="17">
          <el-input v-model="form.name" placeholder="请输入名称" ></el-input>
        </el-col>
      </el-row>
    </el-form-item>
    <el-form-item label="算法模型"  prop="modelType">
      <el-select v-model="form.modelType" >
                <el-option
                v-for="option in options"
                :key="option.value"
                :label="option.label"
                :value="option.value">
                </el-option>
      </el-select>
    </el-form-item>
  </el-form>
  <div slot="footer" class="dialog-footer" >
    <el-button @click="closeDialog">取 消</el-button>
    <el-button type="primary" @click="submit(form)">确 定</el-button>
  </div>
</el-dialog>
  </div>
</template>
<script>
import { mapState } from "vuex";
import { getModelTypes, getModel } from "@/api/getData";
export default {
  name: "AlgorithmAddDialog",
  data() {
    return {
      options: [],
      formLabelWidth: "80px",
      form: {
        modelType: "",
        name: "",
        aggreModelId: 0
      },
      rules: {
        name: [{ required: true, message: "请填写名称", trigger: "blur" }],
        modelType: [
          { required: true, message: "请选择算法类型", trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    closeDialog() {
      this.$store.commit("closeAlgAddDialog");
    },
    async submit(form) {
      this.$refs["AlgorithmAddForm"].validate(async valid => {
        if (valid) {
          if (this.validateName()) {
            this.closeDialog();
            const response = await getModel(form);
            if (response.success) {
              this.$emit("addModel", response.result);
              this.$message("新建模型成功");
            } else {
              this.$message("新建模型失败");
            }
          } else {
            this.$message("算法名称已经存在");
          }
        }
      });
    },
    async initData() {
      try {
        const response = await getModelTypes();
        if (response.success) {
          this.options = response.result;
        }
      } catch (error) {
        console.log(error);
      }
    },
    validateName() {
      let result = this.usedNames.find(usedName => usedName == this.form.name);
      if (result == undefined) {
        return true;
      } else {
        return false;
      }
    }
  },
  computed: {
    AlgorithmAddDialogState: {
      get() {
        return this.$store.state.AlgorithmAddDialogState;
      },
      //只有关闭按钮会调用该函数，所以默认设置为关闭方法
      set: function(newValue) {
        this.$store.commit("closeAlgAddDialog");
      }
    }
  },
  props: {
    usedNames: {}
  },
  created() {
    this.initData();
    this.form.aggreModelId = this.$route.params.id;
  }
};
</script>
