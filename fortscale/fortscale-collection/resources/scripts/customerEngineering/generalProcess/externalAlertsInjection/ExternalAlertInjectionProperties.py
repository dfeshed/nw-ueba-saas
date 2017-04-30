class Properties(object):
    mandatoryFieldMapping = {"Start_time": "time", "End_time": "end_time", "Username": "user_id",
                             "Alert_type": "description", "Alert_description": "description", "Score": "threat_score",
                             "Alert_detail": "detail_json", "Data_source": "splunk_index"}
    fieldToAddToAlertDetail = ["ip_address", "host_name"]
    startTimeFormat = '%Y-%m-%d %H:%M:%S.%f'
    endTimeFormat = "%Y-%m-%d %H:%M:%S.%f"
    commaReplacmentValue = "\',\'###\'|\'@@\', \'###\'| \'@@\", \'###\"| \'@@, ###| "


    def __init__(self):
        pass

    def __str__(self):
        return "mandatoryFieldMapping - " + str(self.mandatoryFieldMapping) + "  fieldToAddToAlertDetail -  " + str(self.fieldToAddToAlertDetail) + "  startTimeFormat - " + self.startTimeFormat \
               + "  endTimeFormat - " + self.endTimeFormat + "  commaReplacmentValue - " + self.commaReplacmentValue


