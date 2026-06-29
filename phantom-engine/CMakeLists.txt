cmake_minimum_required(VERSION 3.25)
project(PhantomEngine VERSION 1.0.0 LANGUAGES CXX C)

set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# Compiler flags for strictness and optimizations
if(MSVC)
    add_compile_options(/W4 /WX /permissive- /Zc:__cplusplus)
else()
    add_compile_options(-Wall -Wextra -Werror -pedantic)
endif()

include(FetchContent)

# Fetch simdjson for high-performance JSON parsing
FetchContent_Declare(
    simdjson
    GIT_REPOSITORY https://github.com/simdjson/simdjson.git
    GIT_TAG        v3.9.2
)
FetchContent_MakeAvailable(simdjson)

add_executable(phantom_engine
    src/main.cpp
    src/oracle.cpp
    src/governor.cpp
    src/cache.cpp
    src/download.cpp
    src/launch.cpp
)

target_include_directories(phantom_engine PRIVATE include)
target_link_libraries(phantom_engine PRIVATE simdjson::simdjson)
