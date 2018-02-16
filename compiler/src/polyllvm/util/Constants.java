package polyllvm.util;

import polyglot.util.CollectionUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * PolyLLVM constants for, e.g., important method names and array data layout.
 */
public class Constants {
    public static final String RUNTIME_PACKAGE = "polyllvm.runtime";
    public static final String RUNTIME_HELPER = RUNTIME_PACKAGE + ".Helper";
    public static final String RUNTIME_ARRAY = RUNTIME_PACKAGE + ".Array";

    public static final String CALLOC = "GC_malloc";
    public static final String ENTRY_TRAMPOLINE =
            "Java_polyllvm_runtime_MainWrapper_main___3Ljava_lang_String_2";
    public static final int LLVM_ADDR_SPACE = 0;

    public static final String PERSONALITY_FUNC = "__java_personality_v0";
    public static final String CREATE_EXCEPTION = "createUnwindException";
    public static final String THROW_EXCEPTION = "throwUnwindException";
    public static final String EXTRACT_EXCEPTION = "extractJavaExceptionObject";

    public static final Set<String> NON_INVOKE_FUNCTIONS = new HashSet<>(CollectionUtil.list(
            CALLOC, CREATE_EXCEPTION, EXTRACT_EXCEPTION
    ));

    public static final int DEBUG_INFO_VERSION = 3;
    public static final int DEBUG_DWARF_VERSION = 2;

    /**
     * Offset from start of Object to the 0th field
     *  * First slot used for dispatch vector
     *  * Second slot used for synchronization variable struct pointer
     */
    public static final int OBJECT_FIELDS_OFFSET = 2;

    /**
     * Offset from start of object to the class dispatch vector
     */
    public static final int DISPATCH_VECTOR_OFFSET = 0;

    /** Offset from start of array object to the length field */
    public static final int ARR_LEN_OFFSET = OBJECT_FIELDS_OFFSET; //Length is immediately after object header
    /** Offset from start of array object to start of elements */
    public static final int ARR_ELEM_OFFSET = OBJECT_FIELDS_OFFSET+1; // Element is after the length field

    /**
     * Offset from start of a class dispatch vector to the 0th method. First
     * slot used for interface dispatch. Second slot used for type information.
     */
    public static final int CLASS_DISP_VEC_OFFSET = 2;
    /**
     * Offset from start of an interface dispatch vector to the 0th method.
     */
    public static final int INTF_DISP_VEC_OFFSET = 0;


    /**
     * The default size of the table for interfaces
     */
    public static final int INTERFACE_TABLE_SIZE = 10;

    public static final String CTOR_VAR_NAME = "llvm.global_ctors";
}
