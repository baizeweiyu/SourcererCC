import clang
import clang.cindex
from clang.cindex import CursorKind
from clang.cindex import Config

def getFunctions(filestring, logging, file_path):
    blocks_linenos = []
    strings = []

    # libclangPath = r'/usr/lib/llvm-3.8/lib/libclang.so.1'
    # if Config.loaded:
    #     pass
    # else:
    #     print('Load')
    #     Config.set_library_path(libclangPath)

    try:
        index = clang.cindex.Index.create()
        tu = index.parse(path='0.c', unsaved_files=[('0.c',filestring)])
    except Exception as e:
        logging.warning("File " + file_path + " cannot be parsed. ..." + str(e))
        return (None, None)
    
    AST_root_node= tu.cursor
    file_string_split = filestring.split('\n')
    linecount = filestring.count("\n")
    if not filestring.endswith("\n"):
        linecount += 1
    ast_list = list(AST_root_node.get_children())
    
    for idx, cur in enumerate(ast_list):
        if cur.kind == CursorKind.FUNCTION_DECL:
            start_lineno = cur.location.line
            if idx == len(ast_list) - 1:
                end_lineno = linecount
            else:
                end_lineno = ast_list[idx+1].location.line - 1
            blocks_linenos.append((start_lineno, end_lineno))
            method_body = '\n'.join(file_string_split[start_lineno-1:end_lineno])
            strings.append(method_body)
            
        elif cur.kind == CursorKind.CLASS_DECL:
            ast_list_in_class = list(cur.get_children())
            for idx_in_class, cur_in_class in enumerate(ast_list_in_class):
                if cur_in_class.kind == CursorKind.CXX_METHOD:
                    start_lineno = cur_in_class.location.line
                    if idx_in_class == len(ast_list_in_class) - 1: 
                        if idx == len(ast_list) - 1:
                            end_lineno = linecount
                        else:
                            end_lineno = ast_list[idx+1].location.line - 1
                        for lineno in range(end_lineno-1, 0, -1):
                            if file_string_split[lineno] and file_string_split[lineno][0]=='}':
                                end_lineno = lineno
                                break
                    else:
                        end_lineno = ast_list_in_class[idx_in_class+1].location.line - 1
                    blocks_linenos.append((start_lineno, end_lineno))
                    method_body = '\n'.join(file_string_split[start_lineno-1:end_lineno])
                    strings.append(method_body)
        
    return (blocks_linenos, strings)

if __name__ == "__main__":
    with open('test.c', encoding='utf-8') as f:
        code = f.read()
    
    A = getFunctions(code, None, None)
    print(A[0])
    print(A[1][0])
