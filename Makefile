# Indicates to sub-Makefiles that it was invoked by this one.
export TOP_LEVEL_MAKEFILE_INVOKED := true

# Platform-specific flags; these defaults can be overriden in defs.<platform> files,
# where <platform> is the result of `uname` in bash.
export SHARED_LIB_EXT := so

# Submodules.
POLYGLOT := lib/polyglot/
JAVACPP_PRESETS := lib/javacpp-presets/
SUBMODULES := $(addsuffix .git,$(POLYGLOT) $(JAVACPP_PRESETS))

# PolyLLVM.
export PLC := $(realpath bin/polyllvmc)

# Hack to allow dependency on PolyLLVM source.
export PLC_SRC := $(realpath $(shell find compiler/src -name "*.java"))

# System JDK.
ifndef JDK7
$(error Please point the JDK7 environment variable to your system JDK 7)
endif
export JDK7 := $(realpath $(JDK7))
export JAVAC := $(JDK7)/bin/javac
export JNI_INCLUDES := \
	-I"$(JDK7)/include" \
	-I"$(JDK7)/include/darwin" \
	-I"$(JDK7)/include/linux"
export JDK7_LIB_PATH := $(JDK7)/jre/lib

# JDK. Use the bare-bones JDK by default.
JDK ?= jdk-lite
export JDK := $(realpath $(JDK))
export JDK_CLASSES := $(JDK)/out/classes

# Runtime.
export RUNTIME := $(realpath runtime)
export RUNTIME_CLASSES := $(RUNTIME)/out/classes

# Clang.
export CLANG := clang++
export SHARED_LIB_FLAGS := -g -lgc -shared -rdynamic

# JDK lib.
export LIBJDK = $(JDK)/out/libjdk.$(SHARED_LIB_EXT)
export LIBJDK_FLAGS = $(SHARED_LIB_FLAGS) -Wl,-rpath,$(RUNTIME)/out

# Runtime lib.
export LIBJVM = $(RUNTIME)/out/libjvm.$(SHARED_LIB_EXT)
export LIBJVM_FLAGS = $(SHARED_LIB_FLAGS)

# Platform-specific overrides.
sinclude defs.$(shell uname)

all: compiler runtime jdk

# Compiler.
compiler: polyglot
	@echo "--- Building compiler ---"
	@ant -S
	@echo

runtime: compiler jdk-classes
	@echo "--- Building runtime ---"
	@$(MAKE) -C $(RUNTIME)
	@echo

jdk-classes:
	@echo "--- Building $(notdir $(JDK)) classes ---"
	@$(MAKE) -C $(JDK) classes
	@echo

jdk: compiler runtime
	@echo "--- Building $(notdir $(JDK)) ---"
	@$(MAKE) -C $(JDK)
	@echo

polyglot: | $(SUBMODULES)
	@echo "--- Building Polyglot ---"
	@cd $(POLYGLOT); ant -S jar
	@echo

$(SUBMODULES):
	git submodule update --init

clean:
	@echo "Cleaning compiler, runtime, and jdk"
	@ant -q -S clean
	@$(MAKE) -s -C $(RUNTIME) clean
	@$(MAKE) -s -C $(JDK) clean

.PHONY: compiler runtime jdk-classes jdk