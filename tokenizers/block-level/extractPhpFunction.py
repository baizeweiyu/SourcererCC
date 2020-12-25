import re

'''fun_def_type:
    fun f(){}
    
    fun f()
    {
        
    }
    
    fun f(){
        
    }
    
    fun f()
    
    {
        
    }
'''


def getFunctions(filestring, logging, file_path):
    comment_inline_p1 = '#'
    comment_inline_p2 = '//'
    comment_inline_p1 = re.escape(comment_inline_p1)
    comment_inline_p2 = re.escape(comment_inline_p2)
    comment_inline_pattern = comment_inline_p1 + '.*?$' + '|' + comment_inline_p2 + '.*?$'

    filestring_copy = filestring
    filestring = re.sub(comment_inline_pattern, '', filestring, flags=re.MULTILINE)

    blocks_linenos = []
    strings = []

    file_string_split = filestring.split('\n')
    file_string_split_copy = filestring_copy.split('\n')
    linecount = filestring.count("\n")
    if not filestring.endswith("\n"):
        linecount += 1

    level = 0
    close_patten = None
    for idx, s in enumerate(file_string_split):
        if level == 0:
            s_temp = s.strip()
            if re.match('(function|public|protected|private) .*\(.*\)', s_temp):
                if s_temp[-1] == '}':  # fun_def_type=1
                    start_lineno = end_lineno = idx + 1
                    blocks_linenos.append((start_lineno, end_lineno))
                    method_body = '\n'.join(file_string_split_copy[start_lineno - 1:end_lineno])
                    strings.append(method_body)
                elif s_temp[-1] == '{':  # fun_def_type=3
                    start_lineno = idx + 1
                    pos = re.search('\S', s).span()[0]
                    close_patten = s[:pos] + '}'
                    level = 2
                elif s_temp[-1] == ')':  # fun_def_type=2/4
                    start_lineno = idx + 1
                    level = 1
        elif level == 1:
            if s.strip() == '{':
                close_patten = s.rstrip()[:-1] + '}'
                level = 2
            else:  # ???
                continue
        else:  # level == 2
            if s.rstrip() == close_patten:
                end_lineno = idx + 1
                blocks_linenos.append((start_lineno, end_lineno))
                method_body = '\n'.join(file_string_split_copy[start_lineno - 1:end_lineno])
                strings.append(method_body)
                level = 0

    return (blocks_linenos, strings)


if __name__ == "__main__":
    for i in range(6):
        file_name = 'testphp/test_code' + str(i) + '.php'
        print('-------------%s----------------' % file_name)
        with open(file_name) as f:
            code = f.read()

        A = getFunctions(code, None, None)
        for i in range(len(A[0])):
            print(A[0][i])
            print(A[1][i])
        import pdb;

        pdb.set_trace()
        print(A)
