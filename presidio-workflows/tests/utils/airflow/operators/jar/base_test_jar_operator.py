# In order to run test locally change the path to:
# '/home/presidio/dev-projects/presidio-core/presidio-workflows'
PATH = '/home/presidio/jenkins/workspace/Presidio-Workflows/presidio-workflows'
JAR_PATH = PATH + '/tests/resources/jars/test.jar'
MAIN_CLASS = 'HelloWorld.Main'


def assert_bash_comment(task, expected_bash_comment, expected_java_args={}):
    """
    Checks whether jar operator build expected_bash_comment 
    :param task: 
    :param expected_bash_comment: 
    :param expected_java_args: 
    :return: 
    """
    task_bash_command = task.bash_command
    main_class_index = task_bash_command.rfind(MAIN_CLASS) + len(MAIN_CLASS)
    bash_command = task.bash_command[:main_class_index]

    assert bash_command == expected_bash_comment

    args = task_bash_command[-(len(task_bash_command)-main_class_index):].strip()
    java_args_dict = {k:v.strip('"') for k,v in [i.split("=",1) for i in args.split(" ")]}

    assert java_args_dict == expected_java_args

