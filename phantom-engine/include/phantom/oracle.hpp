#pragma once

#include "phantom/module.hpp"
#include <mutex>
#include <vector>

namespace phantom {

class HardwareOracle : public IModule {
public:
    HardwareOracle();
    ~HardwareOracle() override = default;

    Result<void> initialize(const HardwareProfile& profile) noexcept override;
    void suspend() noexcept override;
    void resume() noexcept override;

    // Returns a dynamically computed snapshot of the system
    Result<HardwareProfile> get_current_profile() const noexcept;
    
    // Register callbacks that trigger when system hits memory or thermal pressure
    void register_pressure_callback(std::function<void(bool)> cb);

private:
    HardwareProfile m_current_profile;
    std::vector<std::function<void(bool)>> m_pressure_callbacks;
    mutable std::mutex m_mutex;

    // Platform-specific detection routines
    void detect_cpu_topology();
    void detect_ram_capacity();
    void detect_storage_medium();
};

} // namespace phantom
