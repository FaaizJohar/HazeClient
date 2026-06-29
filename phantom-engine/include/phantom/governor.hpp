#pragma once

#include "phantom/module.hpp"
#include "phantom/oracle.hpp"
#include <memory>
#include <atomic>

namespace phantom {

enum class MemoryTier {
    HighEnd,    // >= 8GB
    MidRange,   // >= 4GB
    Feather,    // < 4GB
    Ghost       // Forced minimize
};

class AdaptiveResourceGovernor : public IModule {
public:
    explicit AdaptiveResourceGovernor(std::shared_ptr<HardwareOracle> oracle);
    ~AdaptiveResourceGovernor() override = default;

    Result<void> initialize(const HardwareProfile& profile) noexcept override;
    void suspend() noexcept override;
    void resume() noexcept override;

    // Allocate memory based on the current tier budget
    void* allocate_budgeted(size_t size);
    void free_budgeted(void* ptr, size_t size);

    MemoryTier get_current_tier() const noexcept;
    size_t get_active_worker_threads() const noexcept;

private:
    std::shared_ptr<HardwareOracle> m_oracle;
    std::atomic<MemoryTier> m_current_tier;
    std::atomic<size_t> m_worker_threads;

    void evaluate_tier(const HardwareProfile& profile);
};

} // namespace phantom
