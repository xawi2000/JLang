//Copyright (C) 2018 Cornell University

package jlang.extension;

import polyglot.ast.Lang;
import polyglot.ast.Node;
import polyglot.ast.StringLit;
import polyglot.types.ParsedClassType;

import java.lang.Override;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import jlang.ast.JLangExt;
import jlang.visit.LLVMTranslator;

import static jlang.util.Constants.INTERN_STRING_FUNC;
import static org.bytedeco.javacpp.LLVM.*;

public class JLangStringLitExt extends JLangExt {

    @Override
    public Node leaveTranslateLLVM(LLVMTranslator v) {
        StringLit n = (StringLit) node();

	LLVMValueRef stringVar = translateString(n.value(), v, lang());
        LLVMTypeRef internStringFuncType = v.utils.functionType(v.utils.voidType(), LLVMTypeOf(stringVar));
        LLVMValueRef internString = v.utils.getFunction(INTERN_STRING_FUNC, internStringFuncType);
        v.utils.buildProcCall(internString, stringVar);
        v.addTranslation(n,LLVMConstBitCast(stringVar, v.utils.toLL(n.type())));
        return super.leaveTranslateLLVM(v);
    }

    public static LLVMValueRef translateString(String obj, LLVMTranslator v, Lang l) {
	
	char[] chars = obj.toCharArray();
        
        int sizeOfChar= v.utils.sizeOfType(v.ts.Char());
        ParsedClassType arrayType = v.ts.ArrayObject();
        LLVMValueRef dvGlobal = v.dv.getDispatchVectorFor(arrayType);
        LLVMValueRef length = LLVMConstInt(v.utils.intType(32), chars.length, /*sign-extend*/ 0);
        LLVMValueRef elemSize = LLVMConstInt(v.utils.intType(32), sizeOfChar, /*sign-extend*/ 0);
        LLVMValueRef arrStruct = v.utils.buildConstStruct(length, elemSize);
        List<LLVMValueRef> charTranslated = new ArrayList<>();

        LLVMValueRef sync_vars = LLVMConstNull(v.utils.i8Ptr());

        for (char c : chars)
            charTranslated.add(LLVMConstInt(v.utils.intType(16), c, 0));
        LLVMValueRef[] structBody =
                Stream.concat(Stream.of(dvGlobal, sync_vars, arrStruct), charTranslated.stream())
                        .toArray(LLVMValueRef[]::new);

        LLVMValueRef charArray = v.utils.buildConstStruct(structBody);
        String reduce = intStream(obj.getBytes()).mapToObj(b -> b + "_").reduce("", (s1, s2) -> s1 + s2);
        String charVarName = "_char_arr_" + reduce;
        LLVMValueRef stringLit = v.utils.getGlobal(charVarName, LLVMTypeOf(charArray));
        LLVMSetLinkage(stringLit, LLVMLinkOnceODRLinkage);
        LLVMSetInitializer(stringLit, charArray);

        LLVMValueRef dvString = v.dv.getDispatchVectorFor(v.ts.String());

        LLVMValueRef[] stringLitBody =
                Stream.of(dvString, sync_vars, LLVMConstBitCast(stringLit, v.utils.toLL(arrayType)))
                        .toArray(LLVMValueRef[]::new);

        LLVMValueRef string = v.utils.buildConstStruct(stringLitBody);
	String stringVarName = "_string_lit_" + reduce;
	LLVMValueRef stringVar = v.utils.getGlobal(stringVarName, LLVMTypeOf(string));
	LLVMSetLinkage(stringVar, LLVMLinkOnceODRLinkage);
	LLVMSetInitializer(stringVar, string);
	return stringVar;
	
    }
    public static IntStream intStream(byte[] array) {
        return IntStream.range(0, array.length).map(idx -> array[idx]);
    }
}
