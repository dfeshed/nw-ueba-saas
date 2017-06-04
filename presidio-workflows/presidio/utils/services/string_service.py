def is_blank(value):
    """
    Check if the value is empty
    :param value:  
    :type value: string
    :return: boolean
    """
    if value is not None and value is not '':
        return False
    else:
        return True
