package presidio.data.generators.file.dlpfile;

public enum DLPFILE_GENERATOR_TYPES {

        DEFAULT_GEN (1), SINGLE_USER_GEN (2);

        public final int value;
        DLPFILE_GENERATOR_TYPES(int value){
            this.value = value;
        }
}
