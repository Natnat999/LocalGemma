cmake_minimum_required(VERSION 3.22.1)
project("gemmachat")

# C++ Standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Add llama.cpp as a subdirectory
add_subdirectory(llama.cpp)

# Create shared library for JNI
add_library(${CMAKE_PROJECT_NAME} SHARED
    native-lib.cpp
    llama-android.cpp
)

# Find required libraries
find_library(log-lib log)
find_library(android-lib android)

# Include directories
target_include_directories(${CMAKE_PROJECT_NAME} PRIVATE
    llama.cpp
    llama.cpp/common
    llama.cpp/include
)

# Link libraries
target_link_libraries(${CMAKE_PROJECT_NAME}
    llama
    ${log-lib}
    ${android-lib}
)

# Compiler flags for optimization
target_compile_options(${CMAKE_PROJECT_NAME} PRIVATE
    -O3
    -ffast-math
    -fno-finite-math-only
)