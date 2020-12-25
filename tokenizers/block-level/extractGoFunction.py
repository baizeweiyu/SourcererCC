from ctypes import cdll
from ctypes import c_char_p
import os
import json


def getFunctions(filestring, logging, file_path, go_ast=None, lib=None, LIB_PATH='./goblin.so'):
    if go_ast is None:
        if lib is None:
            lib = cdll.LoadLibrary(LIB_PATH)
        go_ast = lib.go_ast
    go_ast.argtype = c_char_p
    go_ast.restype = c_char_p

    blocks_linenos = []
    strings = []

    try:
        s = go_ast(filestring.encode("utf-8")).decode("utf-8")
        AST_root_node = json.loads(s)
    except Exception as e:
        logging.warning("File " + file_path + " cannot be parsed. " + str(e))
        return (None, None)

    file_string_split = filestring.split('\n')
    linecount = filestring.count("\n")
    if not filestring.endswith("\n"):
        linecount += 1

    ast_list = AST_root_node['declarations']
    for idx, cur in enumerate(ast_list):
        if cur['type'] == 'function' or cur['type'] == 'method':
            start_lineno = cur['position']['line']
            if idx == len(ast_list) - 1:
                end_lineno = linecount
            else:
                end_lineno = ast_list[idx + 1]['position']['line'] - 1
            blocks_linenos.append((start_lineno, end_lineno))
            method_body = '\n'.join(file_string_split[start_lineno - 1:end_lineno])
            strings.append(method_body)

    return (blocks_linenos, strings)


if __name__ == "__main__":
    filename = 'test.go'
    # with open(filename, encoding='utf-8') as f:
    with open(filename) as f:
        code = f.read()

    A = getFunctions(code, None, None)
    for i, pos in enumerate(A[0]):
        print(pos)
        print(A[1][i])
