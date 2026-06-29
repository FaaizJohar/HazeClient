#pragma once

#include <expected>
#include <functional>
#include <string_view>
#include <system_error>
#include <cstdint>

namespace phantom {

// Error type for the engine
struct Error {
    std::string_view message;
    int code;
};

// Standard Result type used across the engine (no exceptions)
template <typename T, typename E = Error>
using Result = std::expected<T, E>;

// Global hardware profile distributed to all modules
struct HardwareProfile {
    uint8_t core_count;
    size_t total_ram_mb;
    bool is_hdd;
    bool memory_pressure;
    bool thermal_throttle;
};

// Base interface for all Phantom Engine modules
class IModule {
public:
    virtual ~IModule() = default;
    
    // Initialize the module with current hardware profile
    virtual Result<void> initialize(const HardwareProfile& profile) noexcept = 0;
    
    // Triggered during Ghost Mode to aggressively release memory
    virtual void suspend() noexcept = 0; 
    
    // Triggered when waking up from Ghost Mode
    virtual void resume() noexcept = 0;
};

} // namespace phantom
