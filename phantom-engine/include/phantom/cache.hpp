#pragma once

#include "phantom/module.hpp"
#include "phantom/governor.hpp"
#include <filesystem>
#include <string_view>
#include <span>

namespace phantom {

class ZeroWasteCacheEngine : public IModule {
public:
    explicit ZeroWasteCacheEngine(std::shared_ptr<AdaptiveResourceGovernor> governor);
    ~ZeroWasteCacheEngine() override = default;

    Result<void> initialize(const HardwareProfile& profile) noexcept override;
    void suspend() noexcept override;
    void resume() noexcept override;

    // Acquire an asset by its SHA-256 hash. Returns local path.
    Result<std::filesystem::path> acquire_asset(std::string_view sha256) noexcept;
    
    // Store data into the cache, addressing it by hash.
    Result<void> store_asset(std::string_view sha256, std::span<const std::byte> data) noexcept;

private:
    std::shared_ptr<AdaptiveResourceGovernor> m_governor;
    std::filesystem::path m_cache_root;

    void compress_cold_tier();
};

} // namespace phantom
